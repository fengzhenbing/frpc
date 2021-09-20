package io.github.zhenbing.frpc.core.annotation;

import io.github.zhenbing.frpc.core.config.*;
import io.github.zhenbing.frpc.repository.common.RepositoryConfig;

/**
 * FrpcConfigConfiguration
 *
 * @author fengzhenbing
 */
@EnableFrpcConfigBindings({
        @EnableFrpcConfigBinding(prefix = ApplicationConfig.FRPC + "." + ApplicationConfig.PREFIX, type = ApplicationConfig.class),
        @EnableFrpcConfigBinding(prefix = RepositoryConfig.FRPC + "." + RepositoryConfig.PREFIX, type = RepositoryConfig.class),
        @EnableFrpcConfigBinding(prefix = ProviderConfig.FRPC + "." + ProviderConfig.PREFIX, type = ProviderConfig.class),
        @EnableFrpcConfigBinding(prefix = ConsumerConfig.FRPC + "." + ConsumerConfig.PREFIX, type = ConsumerConfig.class),
        @EnableFrpcConfigBinding(prefix = ConfigCenterConfig.FRPC + "." + ConfigCenterConfig.PREFIX, type = ConfigCenterConfig.class)
})
public class FrpcConfigConfiguration {
}
