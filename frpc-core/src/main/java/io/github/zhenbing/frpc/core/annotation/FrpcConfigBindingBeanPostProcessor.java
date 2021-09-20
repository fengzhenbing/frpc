package io.github.zhenbing.frpc.core.annotation;

import io.github.zhenbing.frpc.common.config.AbstractConfig;
import io.github.zhenbing.frpc.core.util.PropertySourcesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * FrpcConfigBindingBeanPostProcessor
 *
 * @author fengzhenbing
 */
@Slf4j
public class FrpcConfigBindingBeanPostProcessor implements BeanPostProcessor, EnvironmentAware, PriorityOrdered {

    private ConfigurableEnvironment environment;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AbstractConfig) {
            AbstractConfig frpcConfig = (AbstractConfig) bean;
            Map<String, Object> properties = PropertySourcesUtils.getPrefixedProperties(environment.getPropertySources(), frpcConfig.getFullConfigPrefix());
            frpcConfig.bindProperties(frpcConfig, properties);
            if (log.isDebugEnabled()) {
                log.debug("config -> {}", frpcConfig);
            }
        }
        return bean;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 99;
    }
}
