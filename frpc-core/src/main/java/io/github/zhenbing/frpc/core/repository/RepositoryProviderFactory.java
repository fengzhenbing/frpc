package io.github.zhenbing.frpc.core.repository;

import io.github.zhenbing.frpc.common.spi.FrpcServiceLoader;
import io.github.zhenbing.frpc.repository.common.RepositoryConfig;
import io.github.zhenbing.frpc.repository.provider.api.RepositoryProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RepositoryProviderFactory
 *
 * @author fengzhenbing
 */
public class RepositoryProviderFactory {
    private static final Map<String, RepositoryProvider> REPOSITORY_TYPE_MAP = new ConcurrentHashMap<>();

    static {
        FrpcServiceLoader.register(RepositoryProvider.class);
    }

    public static RepositoryProvider getRepositoryProvider(RepositoryConfig repositoryConfig) {
        if (!REPOSITORY_TYPE_MAP.containsKey(repositoryConfig.getType())) {
            RepositoryProvider repositoryProvider = FrpcServiceLoader.getSingletonServiceInstances(RepositoryProvider.class).stream().filter(e -> e.getType().equals(repositoryConfig.getType())).findFirst().get();
            repositoryProvider.init(repositoryConfig);
            RepositoryClientShutdownHook.addHook(repositoryProvider,repositoryConfig.getProps());
            REPOSITORY_TYPE_MAP.put(repositoryConfig.getType(), repositoryProvider);
            return repositoryProvider;
        }
        return REPOSITORY_TYPE_MAP.get(repositoryConfig.getType());
    }
}
