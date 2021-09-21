package io.github.zhenbing.frpc.core.client;

import io.github.zhenbing.frpc.core.spi.FrpcRequest;
import io.github.zhenbing.frpc.core.spi.FrpcResponse;

import java.io.IOException;

/**
 * RestClient
 *
 * @author fengzhenbing
 */
public interface NetClient {
    /**
     * 发送请求
     *
     * @param req
     * @return
     * @throws IOException
     */
    FrpcResponse sendRequest(FrpcRequest req) throws IOException;
}
