package io.github.zhenbing.frpc.annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import static io.github.zhenbing.frpc.util.AnnotatedBeanDefinitionRegistryUtils.registerBeans;

/**
 * FrpcComponentScanRegistrar
 *
 * @author fengzhenbing
 */
public class FrpcComponentScanRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerBeans(registry, ServiceRegisterPostProcessor.class);
        registerBeans(registry, ServiceDiscoverPostProcessor.class);
    }
}
