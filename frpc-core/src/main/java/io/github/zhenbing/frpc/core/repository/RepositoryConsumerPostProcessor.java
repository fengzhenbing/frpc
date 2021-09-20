package io.github.zhenbing.frpc.core.repository;

import io.github.zhenbing.frpc.core.annotation.Referenced;
import io.github.zhenbing.frpc.core.annotation.ReferencedDesc;
import io.github.zhenbing.frpc.core.client.Frpc;
import io.github.zhenbing.frpc.core.config.ConsumerConfig;
import io.github.zhenbing.frpc.core.loadBalancer.LoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * ServiceRegisterPostProcessor
 *
 * @author fengzhenbing
 */
@Slf4j
public class RepositoryConsumerPostProcessor implements InitializingBean, InstantiationAwareBeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    private ConsumerConfig consumerConfig;

    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);

    public RepositoryConsumerPostProcessor() {
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        InjectionMetadata metadata = findReferencedMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of Referenced dependencies failed", ex);
        }
        return pvs;
    }

    private InjectionMetadata findReferencedMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        // Fall back to class name as cache key, for backwards compatibility with custom callers.
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        // Quick check on the concurrent map first, with minimal locking.
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    metadata = buildReferencingMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }

    private InjectionMetadata buildReferencingMetadata(Class<?> clazz) {
        if (!AnnotationUtils.isCandidateClass(clazz, Referenced.class)) {
            return InjectionMetadata.EMPTY;
        }

        //build referencingMetadata
        List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
        Class<?> targetClass = clazz;
        do {
            final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                if (field.isAnnotationPresent(Referenced.class)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        if (log.isInfoEnabled()) {
                            log.info("Referenced annotation is not supported on static fields: " + field);
                        }
                        return;
                    }
                    Referenced referenced = field.getAnnotation(Referenced.class);
                    ReferencedDesc referencedDesc = ReferencedDesc.buildFromReferenced(referenced);
                    if(Objects.isNull(referenced.interfaceClass()) || referenced.interfaceClass().equals(void.class)) {
                        referencedDesc.setInterfaceClass(field.getType());
                    }
                    referencedDesc.setInterfaceName(referencedDesc.getInterfaceClass().getName());
                    if(StringUtils.isEmpty(referencedDesc.getLoadBalancer())) {
                        referencedDesc.setLoadBalancer(Optional.ofNullable(consumerConfig.getLoadBalancer()).orElse(LoadBalancer.DEFAULT_LOADBANCER));
                    }
                    currElements.add(new ReferencedFieldElement(field, referencedDesc));
                }
            });

            //todo 考虑方法注解

            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);

        return InjectionMetadata.forElements(elements, clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        consumerConfig = this.applicationContext.getBean(ConsumerConfig.class);
    }

    /**
     * Class representing injection information about an annotated field.
     */
    private class ReferencedFieldElement extends InjectionMetadata.InjectedElement {
        private final ReferencedDesc referencedDesc;

        public ReferencedFieldElement(Field field, ReferencedDesc referencedDesc) {
            super(field, null);
            this.referencedDesc = referencedDesc;
        }


        @Override
        protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
            Field field = (Field) this.member;

            //todo value 缓存起来
            Object value = Frpc.createFromRepository(applicationContext, referencedDesc);
            if (Objects.nonNull(value)) {
                Boolean originalAccessible = field.isAccessible();
                ReflectionUtils.makeAccessible(field);
                field.set(bean, value);
                field.setAccessible(originalAccessible);
            }
        }
    }

}
