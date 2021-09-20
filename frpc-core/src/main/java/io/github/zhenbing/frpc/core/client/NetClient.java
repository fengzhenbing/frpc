package io.github.zhenbing.frpc.core.client;

import io.github.zhenbing.frpc.core.api.FrpcRequest;
import io.github.zhenbing.frpc.core.api.FrpcResponse;

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
