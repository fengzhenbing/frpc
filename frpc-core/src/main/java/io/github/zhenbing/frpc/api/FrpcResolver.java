package io.github.zhenbing.frpc.api;

/**
 * FrpcResolver
 *
 * @author fengzhenbing
 */
public interface FrpcResolver {
    Object resolve(String serviceClass);
}
