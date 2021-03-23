package io.github.zhenbing.frpc.registry;

import io.github.zhenbing.frpc.api.ServiceProviderDesc;

import java.util.List;

/**
 * RegistryClient
 *
 * @author fengzhenbing
 */
public interface RegistryClient {

    /**
     * 服务信息注册
     *
     * @param serviceProviderDesc
     */
    void registerService(ServiceProviderDesc serviceProviderDesc);

    /**
     * 服务信息获取
     *
     * @return
     */
    List<ServiceProviderDesc> loadServiceProviderDescList(String className);
}
