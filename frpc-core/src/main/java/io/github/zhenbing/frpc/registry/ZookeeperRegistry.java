package io.github.zhenbing.frpc.registry;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.util.Objects;

/**
 * @Description
 * @Author fzb
 * @date 2021.05.18 00:24
 */
@Slf4j
public class ZookeeperRegistry implements ServiceRegistry {

    private final ZookeeperClient zookeeperClient;

    public ZookeeperRegistry(final ZookeeperClient zookeeperClient) {
        this.zookeeperClient = zookeeperClient;
    }

    @Override
    public void registerService(ServiceDesc serviceDesc) {
        CuratorFramework client = zookeeperClient.getClient();
        try {
            String serviceGroupPath = "/" + serviceDesc.getServiceInterfaceClass();
            if (null == client.checkExists().forPath(serviceGroupPath)) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(serviceGroupPath, serviceDesc.getServiceImplClass().getBytes());
            }
            String serviceItemPath = serviceGroupPath + "/" + serviceDesc.getHost() + "_" + serviceDesc.getPort();
            byte[] serviceDescData = JSON.toJSONBytes(serviceDesc);
            if (Objects.nonNull(client.checkExists().forPath(serviceItemPath))) {
                client.delete().forPath(serviceItemPath);
            }
            client.create().withMode(CreateMode.EPHEMERAL).forPath(serviceItemPath, serviceDescData);
        } catch (Exception ex) {
            log.error("ex ->", ex);
            ex.printStackTrace();
        }
    }

    @Override
    public void unregisterService(ServiceDesc serviceDesc) {

    }
}
