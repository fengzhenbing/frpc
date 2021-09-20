package io.github.zhenbing.frpc.core.loadBalancer;

import io.github.zhenbing.frpc.common.spi.FrpcServiceLoader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LoadBalancerFactory
 *
 * @author fengzhenbing
 */
public class LoadBalancerFactory {
    private static final Map<String, LoadBalancer> LOADBALANCER_TYPE_MAP = new ConcurrentHashMap<>();

    static {
        FrpcServiceLoader.register(LoadBalancer.class);
    }

    public static LoadBalancer getLoadBalancer(String type) {
        if (!LOADBALANCER_TYPE_MAP.containsKey(type)) {
            LoadBalancer loadBalancer = FrpcServiceLoader.getSingletonServiceInstances(LoadBalancer.class).stream().filter(e -> e.getType().equals(type)).findFirst().get();
            LOADBALANCER_TYPE_MAP.put(type, loadBalancer);
            return loadBalancer;
        }
        return LOADBALANCER_TYPE_MAP.get(type);
    }
}
