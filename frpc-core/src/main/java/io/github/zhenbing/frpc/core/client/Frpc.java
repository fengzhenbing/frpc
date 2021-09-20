package io.github.zhenbing.frpc.core.client;

import io.github.zhenbing.frpc.core.api.Filter;
import io.github.zhenbing.frpc.core.api.FrpcRequest;
import io.github.zhenbing.frpc.core.api.Router;
import io.github.zhenbing.frpc.core.api.LoadBalancer;
import io.github.zhenbing.frpc.core.repository.RepositoryConsumerFactory;
import io.github.zhenbing.frpc.repository.common.RepositoryConfig;
import io.github.zhenbing.frpc.repository.common.ServiceDesc;
import io.github.zhenbing.frpc.repository.consumer.api.RepositoryConsumer;
import lombok.extern.slf4j.Slf4j;
import io.github.zhenbing.frpc.core.config.ConsumerConfig;
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


    public static <T, filters> T createFromRepository(ApplicationContext applicationContext, Class<?> serviceInterfaceClass) {
        ConsumerConfig consumerConfig =  applicationContext.getBean(ConsumerConfig.class);
        if (ConsumerConfig.PROXY_BYTE_BUDDY.equals(consumerConfig.getProxy())) {
            return (T) buddyProxy.create(applicationContext,serviceInterfaceClass);
        } else {
            return (T) defaultProxy.create(applicationContext,serviceInterfaceClass);
        }
    }

    public static ServiceDesc getServiceProviderDesc(ApplicationContext applicationContext, Class<?> serviceInterfaceClass) {
        RepositoryConfig repositoryConfig = applicationContext.getBean(RepositoryConfig.class);
        RepositoryConsumer repositoryConsumer = RepositoryConsumerFactory.getRepositoryConsumer(repositoryConfig);

        // 1. 从zk拿到服务提供的列表
        List<ServiceDesc> serviceDescList = repositoryConsumer.loadServiceDescList(serviceInterfaceClass.getName());
        if (CollectionUtils.isEmpty(serviceDescList)) {
            log.error("no service provider for -> {}", serviceInterfaceClass);
            return null;
        }

        List<String> invokers = serviceDescList.stream().map(e -> e.httpUrl()).collect(Collectors.toList());

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
        ServiceDesc serviceDesc = serviceDescList.stream().filter(e -> e.httpUrl().equals(url)).collect(Collectors.toList()).get(0);
        if (serviceDesc == null) {
            log.error("serviceProviderDesc is null");
        }
        return serviceDesc;
    }

    public static <T> T create(final ApplicationContext applicationContext, final Class<T> serviceClass, final ServiceDesc serviceDesc, Filter... filters) {

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
     * @param serviceDesc
     * @return
     */
    public static FrpcRequest buildFrpcRequest(Class serviceClass, Method method, Object[] params, ServiceDesc serviceDesc) {
        FrpcRequest request = new FrpcRequest();
        request.setServiceInterfaceClass(serviceClass.getName());
        request.setServiceImplClass(serviceDesc.getServiceImplClass());
        request.setMethod(method.getName());
        request.setParams(params);
        request.setUrl(serviceDesc.httpUrl());
        return request;
    }
}
