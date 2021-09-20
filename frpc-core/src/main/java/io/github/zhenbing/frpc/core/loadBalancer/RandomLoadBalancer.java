package io.github.zhenbing.frpc.core.loadBalancer;

import io.github.zhenbing.frpc.repository.common.ServiceDesc;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * RandomLoadbalance
 *
 * @author fengzhenbing
 */
@Slf4j
public class RandomLoadBalancer extends AbstractLoadBalancer {

    @Override
    public ServiceDesc select(List<ServiceDesc> serviceDescList) {
        int index = (int) (Math.random() * serviceDescList.size());
        ServiceDesc serviceDesc = serviceDescList.get(index);
        log.info("select index -> {},url -> {}", index, serviceDesc.httpUrl());
        return serviceDesc;
    }

    @Override
    public String getType() {
        return "random";
    }
}
