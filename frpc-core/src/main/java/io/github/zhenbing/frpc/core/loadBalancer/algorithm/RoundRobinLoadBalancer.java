package io.github.zhenbing.frpc.core.loadBalancer.algorithm;

import io.github.zhenbing.frpc.core.loadBalancer.AbstractLoadBalancer;
import io.github.zhenbing.frpc.repository.common.ServiceDesc;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RoundRobinLoadBalancer
 *
 * @author fengzhenbing
 */
@Slf4j
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {
    private final ConcurrentHashMap<String, AtomicInteger> COUNTS = new ConcurrentHashMap<>(64);

    @Override
    public ServiceDesc select(final List<ServiceDesc> serviceDescList) {
        String serviceName = serviceDescList.get(0).getServiceInterfaceClass();
        AtomicInteger count = COUNTS.containsKey(serviceName) ? COUNTS.get(serviceName) : new AtomicInteger(0);
        COUNTS.putIfAbsent(serviceName, count);
        count.compareAndSet(serviceDescList.size(), 0);
        int index = Math.abs(count.getAndIncrement()) % serviceDescList.size();
        ServiceDesc serviceDesc = serviceDescList.get(index);
        log.info("select index -> {},url -> {}", index, serviceDesc.httpUrl());
        return serviceDesc;
    }

    @Override
    public String getType() {
        return "roundRobin";
    }
}
