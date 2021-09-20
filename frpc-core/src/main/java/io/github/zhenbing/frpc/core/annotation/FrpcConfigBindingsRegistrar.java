package io.github.zhenbing.frpc.core.annotation;

import io.github.zhenbing.frpc.common.config.AbstractConfig;
import io.github.zhenbing.frpc.core.util.AnnotatedBeanDefinitionRegistryUtils;
import io.github.zhenbing.frpc.core.util.PropertySourcesUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.Map;

import static io.github.zhenbing.frpc.core.util.PropertySourcesUtils.getPrefixedProperties;

/**
 * FrpcConfigBindingsRegistrar
 *
 * @author fengzhenbing
 */
public class FrpcConfigBindingsRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private ConfigurableEnvironment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableFrpcConfigBindings.class.getName()));

        AnnotationAttributes[] annotationAttributes = attributes.getAnnotationArray("value");

        AnnotatedBeanDefinitionRegistryUtils.registerBeans(registry, FrpcConfigBindingBeanPostProcessor.class);

        for (AnnotationAttributes element : annotationAttributes) {

            String prefix = environment.resolvePlaceholders(element.getString("prefix"));

            Class<? extends AbstractConfig> configClass = element.getClass("type");

            Map<String, Object> properties = PropertySourcesUtils.getPrefixedProperties(environment.getPropertySources(), prefix);

            AnnotatedBeanDefinitionRegistryUtils.registerBeans(registry, configClass);
        }

    }

    @Override
    public void setEnvironment(Environment environment) {
        Assert.isInstanceOf(ConfigurableEnvironment.class, environment);
        this.environment = (ConfigurableEnvironment) environment;
    }
}
