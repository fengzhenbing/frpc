package io.github.zhenbing.frpc.repository.consumer.api;

import io.github.zhenbing.frpc.repository.common.RepositoryClient;
import io.github.zhenbing.frpc.repository.common.ServiceDesc;

import java.util.List;

/**
 * RepositoryConsumer
 *
 * @author fengzhenbing
 */
public interface RepositoryConsumer extends RepositoryClient {

    /**
     * 服务信息获取 客户端使用
     *
     * @return
     */
    List<ServiceDesc> loadServiceDescList(String interfaceName);
}
