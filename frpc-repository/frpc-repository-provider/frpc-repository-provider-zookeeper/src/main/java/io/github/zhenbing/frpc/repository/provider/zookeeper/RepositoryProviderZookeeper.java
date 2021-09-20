package io.github.zhenbing.frpc.repository.provider.zookeeper;

import com.alibaba.fastjson.JSON;
import io.github.zhenbing.frpc.common.config.AbstractConfig;
import io.github.zhenbing.frpc.repository.common.RepositoryConfig;
import io.github.zhenbing.frpc.repository.common.ServiceDesc;
import io.github.zhenbing.frpc.repository.provider.api.RepositoryProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description
 * @Author fzb
 * @date 2021.05.18 00:24
 */
@Slf4j
public class RepositoryProviderZookeeper implements RepositoryProvider {
    private final String type = "zookeeper";

    private CuratorFramework zookeeperClient;

    @Override
    public void init(RepositoryConfig repositoryConfig) {
        // start zk client
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zookeeperClient = CuratorFrameworkFactory.builder()
                .connectString(repositoryConfig.getHost() + ":" + repositoryConfig.getPort())
                .namespace(AbstractConfig.FRPC)
                // 超时时间： 服务断了后， 超过这个时间，临时节点被删除。 限制在 2 到20 个ticktime
                .sessionTimeoutMs(4000)
                .connectionTimeoutMs(4000)
                .retryPolicy(retryPolicy).build();
        zookeeperClient.start();
    }

    @Override
    public String getType() {
        return type;
    }


    @Override
    public void persist(ServiceDesc serviceDesc) {
        try {
            String serviceGroupPath = "/" + serviceDesc.getServiceInterfaceClass();
            if (null == zookeeperClient.checkExists().forPath(serviceGroupPath)) {
                zookeeperClient.create().withMode(CreateMode.PERSISTENT).forPath(serviceGroupPath, serviceDesc.getServiceImplClass().getBytes());
            }
            String serviceItemPath = serviceGroupPath + "/" + serviceDesc.getHost() + "_" + serviceDesc.getPort();
            byte[] serviceDescData = JSON.toJSONBytes(serviceDesc);
            if (Objects.nonNull(zookeeperClient.checkExists().forPath(serviceItemPath))) {
                zookeeperClient.delete().forPath(serviceItemPath);
            }
            zookeeperClient.create().withMode(CreateMode.EPHEMERAL).forPath(serviceItemPath, serviceDescData);
        } catch (Exception ex) {
            log.error("ex ->", ex);
            ex.printStackTrace();
        }
    }


    @Override
    public void close() {
        zookeeperClient.close();
    }
}
