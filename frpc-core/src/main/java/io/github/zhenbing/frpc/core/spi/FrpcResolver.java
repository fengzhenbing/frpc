package io.github.zhenbing.frpc.core.spi;

/**
 * FrpcResolver
 *
 * @author fengzhenbing
 */
public interface FrpcResolver {
    Object resolve(String serviceClass);
}
