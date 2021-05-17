package io.github.zhenbing.frpc.client;

import org.springframework.context.ApplicationContext;

/**
 * FrpcProxy
 *
 * @author fengzhenbing
 */
public interface FrpcProxy {
    /**
     * 代理
     *
     * @param serviceClass
     * @param serviceProviderDesc
     * @param filters
     * @param <T>
     * @return
     */
    <T> T create(ApplicationContext applicationContext, final Class<T> serviceClass);
}
