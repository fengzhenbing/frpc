package io.github.zhenbing.frpc.registry;

import io.github.zhenbing.frpc.config.RegistryCenterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * RegistryClientSupport
 *
 * @author fengzhenbing
 */
@Slf4j
public class RegistryDiscoveryFactory {
    private static ServiceRegistry serviceRegistry;
    private static ServiceDiscovery serviceDiscovery;

    public static ServiceRegistry getServiceRegistry(RegistryCenterConfig registryCenterConfig, ApplicationContext applicationContext) {
        if (StringUtils.isEmpty(registryCenterConfig.getAddress())) {
            log.error("registry center address cannot be null");
            return null;
        }
        if (Objects.isNull(serviceRegistry)) {
            initRegistryClient(registryCenterConfig, applicationContext);
        }
        return serviceRegistry;
    }

    public static ServiceDiscovery getServiceDiscovery(RegistryCenterConfig registryCenterConfig, ApplicationContext applicationContext) {
        if (StringUtils.isEmpty(registryCenterConfig.getAddress())) {
            log.error("registry center address cannot be null");
            return null;
        }
        if (Objects.isNull(serviceDiscovery)) {
            initDiscoveryClient(registryCenterConfig, applicationContext);
        }
        return serviceDiscovery;
    }

    /**
     * initRegistryClient
     *
     * @param registryCenterConfig
     * @param applicationContext
     */
    private static void initRegistryClient(RegistryCenterConfig registryCenterConfig, ApplicationContext applicationContext) {
        if (registryCenterConfig.getAddress().startsWith(RegistryCenterConfig.ZOOKEEPER)) {
            if (Objects.isNull(serviceRegistry)) {
                synchronized (RegistryDiscoveryFactory.class) {
                    if (Objects.isNull(serviceRegistry)) {
                        try {
                            serviceRegistry = applicationContext.getBean(ServiceRegistry.class);
                        } catch (BeansException e) {
                            if (log.isDebugEnabled()) {
                                log.debug("cannot get a serviceRegistry from springContext so that init it instead! ");
                            }
                        }
                        //init serviceRegistry
                        if (Objects.isNull(serviceRegistry)) {
                            ZookeeperClient zookeeperClient = new ZookeeperClient();
                            zookeeperClient.init(registryCenterConfig);
                            serviceRegistry = new ZookeeperRegistry(zookeeperClient);
                        }
                    }
                }
            }
        } else if (registryCenterConfig.getAddress().startsWith(RegistryCenterConfig.NACOS)) {
            //TODO 其他注册中心
        }
    }

    /**
     * initRegistryClient
     *
     * @param registryCenterConfig
     * @param applicationContext
     */
    private static void initDiscoveryClient(RegistryCenterConfig registryCenterConfig, ApplicationContext applicationContext) {
        if (registryCenterConfig.getAddress().startsWith(RegistryCenterConfig.ZOOKEEPER)) {
            if (Objects.isNull(serviceDiscovery)) {
                synchronized (RegistryDiscoveryFactory.class) {
                    if (Objects.isNull(serviceDiscovery)) {
                        try {
                            serviceDiscovery = applicationContext.getBean(ServiceDiscovery.class);
                        } catch (BeansException e) {
                            if (log.isDebugEnabled()) {
                                log.debug("cannot get a serviceDiscovery from springContext so that init it instead! ");
                            }
                        }
                        //init serviceDiscovery
                        if (Objects.isNull(serviceDiscovery)) {
                            ZookeeperClient zookeeperClient = new ZookeeperClient();
                            zookeeperClient.init(registryCenterConfig);
                            serviceDiscovery = new ZookeeperDiscovery(zookeeperClient);
                        }
                    }
                }
            }
        } else if (registryCenterConfig.getAddress().startsWith(RegistryCenterConfig.NACOS)) {
            //TODO 其他注册中心
        }
    }
}
