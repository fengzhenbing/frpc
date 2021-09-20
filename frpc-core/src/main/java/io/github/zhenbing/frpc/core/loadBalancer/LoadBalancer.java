package io.github.zhenbing.frpc.core.loadBalancer;

import io.github.zhenbing.frpc.repository.common.ServiceDesc;

import java.util.List;

/**
 * LoadBalancer
 *
 * @author fengzhenbing
 */
public interface LoadBalancer {

    String DEFAULT_LOADBANCER ="random";

    ServiceDesc select(List<ServiceDesc> serviceDescList);

    String getType();
}
