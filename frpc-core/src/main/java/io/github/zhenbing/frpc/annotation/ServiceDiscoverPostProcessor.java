package io.github.zhenbing.frpc.annotation;

import io.github.zhenbing.frpc.api.Filter;
import io.github.zhenbing.frpc.api.LoadBalancer;
import io.github.zhenbing.frpc.api.Router;
import io.github.zhenbing.frpc.client.Frpc;
import lombok.extern.slf4j.Slf4j;
import io.github.zhenbing.frpc.config.ConsumerConfig;
import io.github.zhenbing.frpc.config.RegistryConfig;
import io.github.zhenbing.frpc.registry.RegistryClient;
import io.github.zhenbing.frpc.registry.RegistryClientFactory;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * ServiceRegisterPostProcessor
 *
 * @author fengzhenbing
 */
@Slf4j
public class ServiceDiscoverPostProcessor implements InitializingBean, InstantiationAwareBeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    private RegistryConfig registryConfig;

    private ConsumerConfig consumerConfig;

    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);

    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<>(4);

    public ServiceDiscoverPostProcessor(){
        this.autowiredAnnotationTypes.add(Referenced.class);
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        InjectionMetadata metadata = findReferencedMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        }
        catch (BeanCreationException ex) {
            throw ex;
        }
        catch (Throwable ex) {
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
        RegistryClient registryClient = RegistryClientFactory.getRegistryClient(registryConfig,applicationContext);
        if(Objects.isNull(registryClient)){
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
                    currElements.add(new ReferencedFieldElement(field,registryClient));

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
        registryConfig = this.applicationContext.getBean(RegistryConfig.class);
        consumerConfig = this.applicationContext.getBean(ConsumerConfig.class);
    }

    /**
     * Class representing injection information about an annotated field.
     */
    private class ReferencedFieldElement extends InjectionMetadata.InjectedElement {
        private RegistryClient registryClient;

        public ReferencedFieldElement(Field field, RegistryClient registryClient) {
            super(field, null);
            this.registryClient = registryClient;
         }


        @Override
        protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
            Field field = (Field) this.member;

            Router router = null;
            try {
                router =  applicationContext.getBean(Router.class);
            }catch (BeansException e){
                if(log.isDebugEnabled()){
                    log.debug(" no router configured ");
                }
            }
            LoadBalancer loadBalancer = null;
            try {
                Object o  =  applicationContext.getBean("randomLoadbalance");
                loadBalancer =  applicationContext.getBean(LoadBalancer.class);
            }catch (BeansException e){
                if(log.isDebugEnabled()){
                    log.debug(" no loadBalancer configured ");
                }
            }
            Filter[] filters = new Filter[]{};
            try {
                Map<String, Filter> filterMap = applicationContext.getBeansOfType(Filter.class);
                if(!CollectionUtils.isEmpty(filterMap)){
                   List filterList = filterMap.entrySet().stream().map(e->e.getValue()).collect(Collectors.toList());
                   filterList.toArray(filters);
                }
            }catch (BeansException e){
                if(log.isDebugEnabled()){
                    log.debug(" no filter configured ");
                }
            }

            //todo value 缓存起来
            Object value = Frpc.createFromRegistry(field.getType(),consumerConfig,registryClient,router,loadBalancer,filters);
            if (Objects.nonNull(value)) {
                ReflectionUtils.makeAccessible(field);
                field.set(bean, value);
            }
        }
    }

}
