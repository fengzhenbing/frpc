package io.github.zhenbing.frpc.demo.consumer.frpc;

import io.github.zhenbing.frpc.core.spi.Filter;
import io.github.zhenbing.frpc.core.spi.FrpcRequest;
import org.springframework.stereotype.Component;

/**
 * HeaderFilter
 *
 * @author fengzhenbing
 */
@Component
public class HeaderFilter implements Filter {
    @Override
    public boolean filter(FrpcRequest request) {
        request.getHeaders().put("token", "fengzhenbing");
        return true;
    }
}
