package io.github.zhenbing.frpc.annotation;

import io.github.zhenbing.frpc.client.Frpc;
import io.github.zhenbing.frpc.registry.ServiceDiscovery;
import io.github.zhenbing.frpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import io.github.zhenbing.frpc.config.RegistryCenterConfig;
import io.github.zhenbing.frpc.registry.RegistryDiscoveryFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
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
public class ServiceDiscoverPostProcessor implements InitializingBean, InstantiationAwareBeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    private RegistryCenterConfig registryCenterConfig;

    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);

    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<>(4);

    public ServiceDiscoverPostProcessor() {
        this.autowiredAnnotationTypes.add(Referenced.class);
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
        if (!AnnotationUtils.isCandidateClass(clazz, this.autowiredAnnotationTypes)) {
            return InjectionMetadata.EMPTY;
        }

        //init registryClient
        ServiceRegistry registryCenterClient = RegistryDiscoveryFactory.getServiceRegistry(registryCenterConfig, applicationContext);
        if (Objects.isNull(registryCenterClient)) {
            log.error("cannot find a registryClient");
            return InjectionMetadata.EMPTY;
        }

        //build referencingMetadata
        List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
        Class<?> targetClass = clazz;
        do {
            final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                MergedAnnotation<?> ann = findReferencedAnnotation(field);
                if (ann != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        if (log.isInfoEnabled()) {
                            log.info("Referenced annotation is not supported on static fields: " + field);
                        }
                        return;
                    }
                    currElements.add(new ReferencedFieldElement(field));

                }
            });

            //todo 考虑方法注解

            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);

        return InjectionMetadata.forElements(elements, clazz);
    }


    @Nullable
    private MergedAnnotation<?> findReferencedAnnotation(AccessibleObject ao) {
        MergedAnnotations annotations = MergedAnnotations.from(ao);
        for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
            MergedAnnotation<?> annotation = annotations.get(type);
            if (annotation.isPresent()) {
                return annotation;
            }
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        registryCenterConfig = this.applicationContext.getBean(RegistryCenterConfig.class);
    }

    /**
     * Class representing injection information about an annotated field.
     */
    private class ReferencedFieldElement extends InjectionMetadata.InjectedElement {

        public ReferencedFieldElement(Field field) {
            super(field, null);
        }


        @Override
        protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
            Field field = (Field) this.member;

            //todo value 缓存起来
            Object value = Frpc.createFromRegistry(applicationContext, field.getType());
            if (Objects.nonNull(value)) {
                ReflectionUtils.makeAccessible(field);
                field.set(bean, value);
            }
        }
    }

}
