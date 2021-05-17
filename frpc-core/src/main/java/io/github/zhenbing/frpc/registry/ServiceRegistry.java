package io.github.zhenbing.frpc.registry;

public interface ServiceRegistry {

    /**
     * 服务信息注册 服务提供者使用
     *
     * @param serviceDesc
     */
    void registerService(ServiceDesc serviceDesc);

    /**
     * 服务信息取消注册 服务提供者使用
     *
     * @param serviceDesc
     */
    void unregisterService(ServiceDesc serviceDesc);
}
