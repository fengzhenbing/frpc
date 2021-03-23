package io.github.zhenbing.frpc.client;

import io.github.zhenbing.frpc.api.FrpcRequest;
import io.github.zhenbing.frpc.api.FrpcResponse;

import java.io.IOException;

/**
 * RestClient
 *
 * @author fengzhenbing
 */
public interface NetClient {
    FrpcResponse sendRequest(FrpcRequest req) throws IOException;
}
