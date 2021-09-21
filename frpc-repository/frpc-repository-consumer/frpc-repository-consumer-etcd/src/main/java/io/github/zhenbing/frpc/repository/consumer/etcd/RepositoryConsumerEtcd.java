package io.github.zhenbing.frpc.repository.consumer.etcd;

import io.github.zhenbing.frpc.repository.common.RepositoryConfig;
import io.github.zhenbing.frpc.repository.common.ServiceDesc;
import io.github.zhenbing.frpc.repository.consumer.api.RepositoryConsumer;

import java.util.List;

/**
 * RepositoryConsumerEtcd
 *
 * @author fengzhenbing
 */
public class RepositoryConsumerEtcd  implements RepositoryConsumer {
    @Override
    public void init(RepositoryConfig repositoryConfig) {

    }

    @Override
    public List<ServiceDesc> loadServiceDescList(String interfaceName) {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }
}
