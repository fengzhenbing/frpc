package io.github.zhenbing.frpc.api;

/**
 * Filter
 *
 * @author fengzhenbing
 */
public interface Filter {
    boolean filter(FrpcRequest request);
}
