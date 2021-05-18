package io.github.zhenbing.frpc.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import io.github.zhenbing.frpc.config.AbstractConfig;
import io.github.zhenbing.frpc.config.RegistryCenterConfig;

/**
 * ZookeeperClient
 *
 * @author fengzhenbing
 */
@Slf4j
public class ZookeeperClient implements RegistryCenterClient {

    private CuratorFramework client;

    @Override
    public void init(RegistryCenterConfig registryCenterConfig) {
        // start zk client
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(registryCenterConfig.getHost() + ":" + registryCenterConfig.getPort())
                .namespace(AbstractConfig.FRPC)
                .retryPolicy(retryPolicy).build();
        client.start();
    }

    @Override
    public void close() {
        client.close();
    }

    public CuratorFramework getClient() {
        return client;
    }
}
