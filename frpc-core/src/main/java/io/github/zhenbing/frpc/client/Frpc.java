package io.github.zhenbing.frpc.client;

import io.github.zhenbing.frpc.api.*;
import io.github.zhenbing.frpc.config.RegistryConfig;
import io.github.zhenbing.frpc.registry.RegistryClientFactory;
import lombok.extern.slf4j.Slf4j;
import io.github.zhenbing.frpc.config.ConsumerConfig;
import io.github.zhenbing.frpc.registry.RegistryClient;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Frpc
 *
 * @author fengzhenbing
 */
@Slf4j
public class Frpc {

    private static final FrpcProxy defaultProxy = new JdkProxy();
    private static final BuddyProxy buddyProxy = new BuddyProxy();


    public static <T, filters> T createFromRegistry(ApplicationContext applicationContext, Class<?> serviceInterfaceClass) {
        ConsumerConfig consumerConfig =  applicationContext.getBean(ConsumerConfig.class);
        if (ConsumerConfig.PROXY_BYTE_BUDDY.equals(consumerConfig.getProxy())) {
            return (T) buddyProxy.create(applicationContext,serviceInterfaceClass);
        } else {
            return (T) defaultProxy.create(applicationContext,serviceInterfaceClass);
        }
    }

    public static ServiceProviderDesc getServiceProviderDesc(ApplicationContext applicationContext, Class<?> serviceInterfaceClass) {
        RegistryConfig registryConfig = applicationContext.getBean(RegistryConfig.class);
        RegistryClient registryClient = RegistryClientFactory.getRegistryClient(registryConfig, applicationContext);

        // 1. 从zk拿到服务提供的列表
        List<ServiceProviderDesc> serviceProviderDescList = registryClient.loadServiceProviderDescList(serviceInterfaceClass.getName());
        if (CollectionUtils.isEmpty(serviceProviderDescList)) {
            log.error("no service provider for -> {}", serviceInterfaceClass);
            return null;
        }

        List<String> invokers = serviceProviderDescList.stream().map(e -> e.httpUrl()).collect(Collectors.toList());

        // 路由匹配
        Router router = getRouter(applicationContext);
        if (Objects.nonNull(router)) {
            invokers = router.route(invokers);
        }

        // 负载均衡
        String url;
        LoadBalancer loadBalancer = getLoadBalancer(applicationContext);
        if (Objects.nonNull(loadBalancer)) {
            url = loadBalancer.select(invokers);
        } else {
            url = invokers.get(0);
        }
        ServiceProviderDesc serviceProviderDesc = serviceProviderDescList.stream().filter(e -> e.httpUrl().equals(url)).collect(Collectors.toList()).get(0);
        if (serviceProviderDesc == null) {
            log.error("serviceProviderDesc is null");
        }
        return serviceProviderDesc;
    }

    public static <T> T create(final ApplicationContext applicationContext,final Class<T> serviceClass, final ServiceProviderDesc serviceProviderDesc, Filter... filters) {

        // 0. 替换动态代理 -> AOP
        return (T) defaultProxy.create(applicationContext,serviceClass);

    }

    /**
     * 获取过滤器
     *
     * @param applicationContext
     * @return filters
     */
    public static Filter[] getFilters(ApplicationContext applicationContext) {
        Filter[] filters = new Filter[]{};
        try {
            Map<String, Filter> filterMap = applicationContext.getBeansOfType(Filter.class);
            if (!CollectionUtils.isEmpty(filterMap)) {
                List filterList = filterMap.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
                filterList.toArray(filters);
            }
        } catch (BeansException e) {
            if (log.isDebugEnabled()) {
                log.debug(" no filter configured ");
            }
        }
        return filters;
    }

    private static LoadBalancer getLoadBalancer(ApplicationContext applicationContext) {
        LoadBalancer loadBalancer = null;
        try {
            // Object o  =  applicationContext.getBean("randomLoadbalance");
            loadBalancer = applicationContext.getBean(LoadBalancer.class);
        } catch (BeansException e) {
            if (log.isDebugEnabled()) {
                log.debug(" no loadBalancer configured ");
            }
        }
        return loadBalancer;
    }

    private static Router getRouter(ApplicationContext applicationContext) {
        Router router = null;
        try {
            router = applicationContext.getBean(Router.class);
        } catch (BeansException e) {
            if (log.isDebugEnabled()) {
                log.debug(" no router configured ");
            }
        }
        return router;
    }


    /**
     * 构造请求
     *
     * @param serviceClass
     * @param method
     * @param params
     * @param serviceProviderDesc
     * @return
     */
    public static FrpcRequest buildFrpcRequest(Class serviceClass, Method method, Object[] params, ServiceProviderDesc serviceProviderDesc) {
        FrpcRequest request = new FrpcRequest();
        request.setServiceInterfaceClass(serviceClass.getName());
        request.setServiceImplClass(serviceProviderDesc.getServiceImplClass());
        request.setMethod(method.getName());
        request.setParams(params);
        request.setUrl(serviceProviderDesc.httpUrl());
        return request;
    }
}
