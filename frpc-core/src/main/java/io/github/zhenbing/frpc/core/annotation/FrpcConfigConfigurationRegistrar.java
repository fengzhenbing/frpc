package io.github.zhenbing.frpc.core.annotation;

import io.github.zhenbing.frpc.core.util.AnnotatedBeanDefinitionRegistryUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * FrpcConfigConfigurationRegistrar
 *
 * @author fengzhenbing
 */
public class FrpcConfigConfigurationRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotatedBeanDefinitionRegistryUtils.registerBeans(registry, FrpcConfigConfiguration.class);
    }
}
