package io.github.zhenbing.frpc.annotation;

import io.github.zhenbing.frpc.registry.ServiceDesc;
import io.github.zhenbing.frpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import io.github.zhenbing.frpc.config.ProviderConfig;
import io.github.zhenbing.frpc.config.RegistryCenterConfig;
import io.github.zhenbing.frpc.registry.RegistryDiscoveryFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * ServiceRegisterPostProcessor
 *
 * @author fengzhenbing
 */
@Slf4j
public class ServiceRegisterPostProcessor implements InitializingBean, BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    private RegistryCenterConfig registryCenterConfig;

    private ProviderConfig providerConfig;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Service.class)) {
            ServiceRegistry registryCenterClient = RegistryDiscoveryFactory.getServiceRegistry(registryCenterConfig, applicationContext);
            if (Objects.isNull(registryCenterClient)) {
                log.error("cannot find a registryClient");
                return bean;
            }

            String serviceHost = providerConfig.getHost();
            try {
                if (StringUtils.isEmpty(serviceHost)) {
                    serviceHost = InetAddress.getLocalHost().getHostAddress();
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            String serviceInterfaceName = bean.getClass().getInterfaces()[0].getName();
            ServiceDesc serviceDesc = ServiceDesc.builder()
                    .host(serviceHost)
                    .port(providerConfig.getPort()).serviceImplClass(bean.getClass().getName()).serviceInterfaceClass(serviceInterfaceName).build();
            registryCenterClient.registerService(serviceDesc);

        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        registryCenterConfig = this.applicationContext.getBean(RegistryCenterConfig.class);
        providerConfig = this.applicationContext.getBean(ProviderConfig.class);
    }

}
