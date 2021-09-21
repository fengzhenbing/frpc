package io.github.zhenbing.frpc.repository.provider.etcd;

import io.github.zhenbing.frpc.repository.common.RepositoryConfig;
import io.github.zhenbing.frpc.repository.common.ServiceDesc;
import io.github.zhenbing.frpc.repository.provider.api.RepositoryProvider;

/**
 * ProviderRepositoryEtcd
 *
 * @author fengzhenbing
 */
public class ProviderRepositoryEtcd implements RepositoryProvider {

    @Override
    public void init(RepositoryConfig repositoryConfig) {

    }

    @Override
    public void persist(ServiceDesc serviceDesc) {

    }

    @Override
    public String getType() {
        return null;
    }
}
