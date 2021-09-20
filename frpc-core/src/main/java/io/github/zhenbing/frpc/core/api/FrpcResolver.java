package io.github.zhenbing.frpc.core.api;

/**
 * FrpcResolver
 *
 * @author fengzhenbing
 */
public interface FrpcResolver {
    Object resolve(String serviceClass);
}
