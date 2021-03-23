package io.github.zhenbing.frpc.annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import static io.github.zhenbing.frpc.util.AnnotatedBeanDefinitionRegistryUtils.registerBeans;

/**
 * FrpcConfigConfigurationRegistrar
 *
 * @author fengzhenbing
 */
public class FrpcConfigConfigurationRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerBeans(registry, FrpcConfigConfiguration.class);
    }
}
