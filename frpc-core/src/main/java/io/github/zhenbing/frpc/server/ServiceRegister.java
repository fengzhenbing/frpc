package io.github.zhenbing.frpc.server;

import io.github.zhenbing.frpc.registry.RegistryClient;

/**
 * ServiceRegister
 *
 * @author fengzhenbing
 */
public interface ServiceRegister {

    /**
     * 注册服务
     *
     * @param client
     * @param service
     */
    void registerService(RegistryClient client, String service);
}
