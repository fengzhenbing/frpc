package io.github.zhenbing.frpc.core.repository;

import io.github.zhenbing.frpc.core.annotation.Service;
import io.github.zhenbing.frpc.repository.common.RepositoryConfig;
import io.github.zhenbing.frpc.repository.common.ServiceDesc;
import io.github.zhenbing.frpc.repository.provider.api.RepositoryProvider;
import lombok.extern.slf4j.Slf4j;
import io.github.zhenbing.frpc.core.config.ProviderConfig;
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
public class RepositoryProviderPostProcessor implements InitializingBean, BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    private RepositoryConfig repositoryConfig;

    private ProviderConfig providerConfig;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Service.class)) {
            RepositoryProvider repositoryProvider = RepositoryProviderFactory.getRepositoryProvider(repositoryConfig);
            Objects.requireNonNull(repositoryProvider,"repositoryProvider does not load");

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
                    .port(providerConfig.getPort())
                    .serviceImplClass(bean.getClass().getName())
                    .serviceInterfaceClass(serviceInterfaceName).build();
            repositoryProvider.persist(serviceDesc);

        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        repositoryConfig = this.applicationContext.getBean(RepositoryConfig.class);
        providerConfig = this.applicationContext.getBean(ProviderConfig.class);
    }

}
