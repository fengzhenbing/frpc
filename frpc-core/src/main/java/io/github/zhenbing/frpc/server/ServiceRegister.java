package io.github.zhenbing.frpc.server;

import io.github.zhenbing.frpc.registry.RegistryClient;

/**
 * ServiceRegister
 *
 * @author fengzhenbing
 */
public interface ServiceRegister {
    void registerService(RegistryClient client, String service);
}
