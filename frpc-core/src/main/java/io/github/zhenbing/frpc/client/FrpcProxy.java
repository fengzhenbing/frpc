package io.github.zhenbing.frpc.client;

import io.github.zhenbing.frpc.api.Filter;
import io.github.zhenbing.frpc.api.ServiceProviderDesc;

/**
 * FrpcProxy
 *
 * @author fengzhenbing
 */
public interface FrpcProxy {
    <T> T create(final Class<T> serviceClass, final ServiceProviderDesc serviceProviderDesc, final Filter... filters);
}
