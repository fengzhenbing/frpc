package io.github.zhenbing.frpc.repository.provider.api;

import io.github.zhenbing.frpc.repository.common.RepositoryClient;
import io.github.zhenbing.frpc.repository.common.ServiceDesc;

/**
 * RepositoryProvider
 *
 * @author fengzhenbing
 */
public interface RepositoryProvider extends RepositoryClient {

    /**
     * 保存服务信息 服务提供者使用
     *
     * @param serviceDesc
     */
    void persist(ServiceDesc serviceDesc);

}
