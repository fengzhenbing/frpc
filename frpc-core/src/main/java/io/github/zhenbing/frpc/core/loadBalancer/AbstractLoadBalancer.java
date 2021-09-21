package io.github.zhenbing.frpc.core.loadBalancer;

import io.github.zhenbing.frpc.repository.common.ServiceDesc;

/**
 * AbstractLoadBalancer
 *
 * @author fengzhenbing
 */
public abstract class AbstractLoadBalancer implements LoadBalancer {

    protected int getWeight(final ServiceDesc serviceDesc) {
        return serviceDesc.getWeight();
    }
}
