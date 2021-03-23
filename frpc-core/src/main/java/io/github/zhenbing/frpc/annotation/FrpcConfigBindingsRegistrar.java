package io.github.zhenbing.frpc.annotation;

import io.github.zhenbing.frpc.config.AbstractConfig;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.Map;

import static io.github.zhenbing.frpc.util.AnnotatedBeanDefinitionRegistryUtils.registerBeans;
import static io.github.zhenbing.frpc.util.PropertySourcesUtils.getPrefixedProperties;

/**
 * FrpcConfigBindingsRegistrar
 *
 * @author fengzhenbing
 */
public class FrpcConfigBindingsRegistrar  implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private ConfigurableEnvironment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableFrpcConfigBindings.class.getName()));

        AnnotationAttributes[] annotationAttributes = attributes.getAnnotationArray("value");

        registerBeans(registry, FrpcConfigBindingBeanPostProcessor.class);

        for (AnnotationAttributes element : annotationAttributes) {

            String prefix = environment.resolvePlaceholders(element.getString("prefix"));

            Class<? extends AbstractConfig> configClass = element.getClass("type");

            Map<String, Object> properties = getPrefixedProperties(environment.getPropertySources(), prefix);

            registerBeans(registry, configClass);
        }

    }

    @Override
    public void setEnvironment(Environment environment) {
        Assert.isInstanceOf(ConfigurableEnvironment.class, environment);
        this.environment = (ConfigurableEnvironment) environment;
    }
}
