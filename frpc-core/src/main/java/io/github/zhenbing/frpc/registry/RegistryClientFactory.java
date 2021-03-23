package io.github.zhenbing.frpc.registry;

import io.github.zhenbing.frpc.config.RegistryConfig;
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
public class RegistryClientFactory {
    private static RegistryClient registryClient;

    public static RegistryClient getRegistryClient(RegistryConfig registryConfig, ApplicationContext applicationContext){
        if(StringUtils.isEmpty(registryConfig.getAddress())){
            log.error("registry address cannot be null");
            return null;
        }
        if(registryConfig.getAddress().startsWith(RegistryConfig.ZOOKEEPER)){
            if(Objects.isNull(registryClient)){
                synchronized (RegistryClientFactory.class){
                    if(Objects.isNull(registryClient)){
                        try {
                            registryClient = applicationContext.getBean(ZookeeperRegistryClient.class);
                        }catch (BeansException e){
                            if(log.isDebugEnabled()){
                                log.debug("cannot get a zookeeperRegistryClient from springContext so that init it instead! ");
                            }
                        }
                        //init registryClient
                        if(Objects.isNull(registryClient)){
                            registryClient = new ZookeeperRegistryClient(registryConfig);
                        }
                    }
                }
            }


        }else if(registryConfig.getAddress().startsWith(RegistryConfig.NACOS)){
            //TODO 其他注册中心
        }
        return registryClient;
    }
}
