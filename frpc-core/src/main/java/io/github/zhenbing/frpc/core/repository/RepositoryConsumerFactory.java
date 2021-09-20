package io.github.zhenbing.frpc.core.repository;

import io.github.zhenbing.frpc.common.spi.FrpcServiceLoader;
import io.github.zhenbing.frpc.repository.common.RepositoryConfig;
import io.github.zhenbing.frpc.repository.consumer.api.RepositoryConsumer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RepositoryConsumerFactory
 *
 * @author fengzhenbing
 */
public class RepositoryConsumerFactory {
    private static final Map<String, RepositoryConsumer> REPOSITORY_TYPE_MAP = new ConcurrentHashMap<>();

    static {
        FrpcServiceLoader.register(RepositoryConsumer.class);
    }

    public static RepositoryConsumer getRepositoryConsumer(RepositoryConfig repositoryConfig) {
        if (!REPOSITORY_TYPE_MAP.containsKey(repositoryConfig.getType())) {
            RepositoryConsumer repositoryConsumer = FrpcServiceLoader.getSingletonServiceInstances(RepositoryConsumer.class).stream().filter(e -> e.getType().equals(repositoryConfig.getType())).findFirst().get();
            repositoryConsumer.init(repositoryConfig);
            RepositoryClientShutdownHook.addHook(repositoryConsumer,repositoryConfig.getProps());
            REPOSITORY_TYPE_MAP.put(repositoryConfig.getType(), repositoryConsumer);
            return repositoryConsumer;
        }
        return REPOSITORY_TYPE_MAP.get(repositoryConfig.getType());
    }
}
