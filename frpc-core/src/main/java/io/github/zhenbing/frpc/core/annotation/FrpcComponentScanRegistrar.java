package io.github.zhenbing.frpc.core.annotation;

import io.github.zhenbing.frpc.core.repository.RepositoryConsumerPostProcessor;
import io.github.zhenbing.frpc.core.repository.RepositoryProviderPostProcessor;
import io.github.zhenbing.frpc.core.util.AnnotatedBeanDefinitionRegistryUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * FrpcComponentScanRegistrar
 *
 * @author fengzhenbing
 */
public class FrpcComponentScanRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotatedBeanDefinitionRegistryUtils.registerBeans(registry, RepositoryProviderPostProcessor.class);
        AnnotatedBeanDefinitionRegistryUtils.registerBeans(registry, RepositoryConsumerPostProcessor.class);
    }
}
