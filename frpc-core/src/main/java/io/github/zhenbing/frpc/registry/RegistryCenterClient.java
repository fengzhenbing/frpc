package io.github.zhenbing.frpc.registry;

import io.github.zhenbing.frpc.config.RegistryCenterConfig;

/**
 * RegistryClient
 *
 * @author fengzhenbing
 */
public interface RegistryCenterClient {

    void init(RegistryCenterConfig registryCenterConfig);

    void close();
}
