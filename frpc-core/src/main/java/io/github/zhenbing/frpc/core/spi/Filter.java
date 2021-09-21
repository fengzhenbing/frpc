package io.github.zhenbing.frpc.core.spi;

import org.springframework.core.Ordered;

/**
 * Filter
 *
 * @author fengzhenbing
 */
public interface Filter extends Ordered {
    boolean filter(FrpcRequest request);

    @Override
    default int getOrder(){
        return Ordered.LOWEST_PRECEDENCE;
    }
}
