package io.github.zhenbing.frpc.demo.provider.frpc;

import io.github.zhenbing.frpc.core.spi.FrpcResolver;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * SpringContextFrpcResolver
 *
 * @author fengzhenbing
 */
public class SpringContextFrpcResolver implements FrpcResolver, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public Object resolve(String serviceClass) {
        try {
            Class clazz = Class.forName(serviceClass);
            return applicationContext.getBean(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
