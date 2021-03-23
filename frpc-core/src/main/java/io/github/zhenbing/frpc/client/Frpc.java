package io.github.zhenbing.frpc.client;

import com.alibaba.fastjson.parser.ParserConfig;
 import lombok.extern.slf4j.Slf4j;
import io.github.zhenbing.frpc.api.Filter;
import io.github.zhenbing.frpc.api.LoadBalancer;
import io.github.zhenbing.frpc.api.Router;
import io.github.zhenbing.frpc.api.ServiceProviderDesc;
import io.github.zhenbing.frpc.config.ConsumerConfig;
import io.github.zhenbing.frpc.registry.RegistryClient;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Frpc
 *
 * @author fengzhenbing
 */
@Slf4j
public class Frpc  {
    static {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        // ParserConfig.getGlobalInstance().addAccept("io.github.zhenbing");
    }

    private static final FrpcProxy defaultProxy = new JdkProxy();
    private static final BuddyProxy buddyProxy = new BuddyProxy();


    public static <T, filters> T createFromRegistry(Class<?> serviceInterfaceClass, ConsumerConfig consumerConfig, RegistryClient registryClient, Router router, LoadBalancer loadBalancer, Filter[] filters) {

        // 加filte之一


        // 1. 简单：从zk拿到服务提供的列表
        List<ServiceProviderDesc> serviceProviderDescList =  registryClient.loadServiceProviderDescList(serviceInterfaceClass.getName());
        if(CollectionUtils.isEmpty(serviceProviderDescList)){
            log.error("no service provider for -> {}", serviceInterfaceClass);
            return null;
        }

        List<String> invokers = serviceProviderDescList.stream().map(e->e.httpUrl()).collect(Collectors.toList());
        // 2. 挑战：监听zk的临时节点，根据事件更新这个list（注意，需要做个全局map保持每个服务的提供者List）
        if(Objects.nonNull(router)){
            invokers = router.route(invokers);
        }

        // loadBalance
        String url;
        if(Objects.nonNull(loadBalancer)){
            url = loadBalancer.select(invokers);
        }else {
            url = invokers.get(0);
        }

        ServiceProviderDesc serviceProviderDesc = serviceProviderDescList.stream().filter(e->e.httpUrl().equals(url)).collect(Collectors.toList()).get(0);

        if(ConsumerConfig.PROXY_BYTE_BUDDY.equals(consumerConfig.getProxy())){
            return (T) buddyProxy.create(serviceInterfaceClass, serviceProviderDesc, filters);
        }else {
            return (T) defaultProxy.create(serviceInterfaceClass, serviceProviderDesc, filters);
        }


    }


    public static <T> T create(final Class<T> serviceClass, final ServiceProviderDesc serviceProviderDesc, Filter... filters) {

        // 0. 替换动态代理 -> AOP
        return (T) defaultProxy.create(serviceClass, serviceProviderDesc, filters);

    }


}
