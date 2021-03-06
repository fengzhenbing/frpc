package io.github.zhenbing.frpc.core.spi;

import io.github.zhenbing.frpc.repository.common.ServiceDesc;

import java.util.List;

/**
 * Router
 *
 * @author fengzhenbing
 */
public interface Router {
    List<ServiceDesc> route(List<ServiceDesc> serviceDescList);
}
