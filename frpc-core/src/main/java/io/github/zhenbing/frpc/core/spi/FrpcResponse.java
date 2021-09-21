package io.github.zhenbing.frpc.core.spi;

import lombok.Data;

/**
 * FrpcResponse
 *
 * @author fengzhenbing
 */
@Data
public class FrpcResponse {
    private Object result;
    private boolean status;
    private Exception exception;
}

