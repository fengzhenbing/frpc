package io.github.zhenbing.frpc.registry;

import java.util.List;

public interface ServiceDiscovery {
    /**
     * 服务信息获取 客户端使用
     *
     * @return
     */
    List<ServiceDesc> loadServiceDescList(String interfaceName);
}
