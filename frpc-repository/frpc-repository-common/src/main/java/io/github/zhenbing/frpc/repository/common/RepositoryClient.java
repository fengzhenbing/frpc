package io.github.zhenbing.frpc.repository.common;

/**
 * RepositoryClient
 *
 * @author fengzhenbing
 */
public interface RepositoryClient extends AutoCloseable{

    void init(RepositoryConfig repositoryConfig);

    String getType();

    default void close() {
    }
}
