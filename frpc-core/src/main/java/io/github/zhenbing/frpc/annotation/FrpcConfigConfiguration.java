package io.github.zhenbing.frpc.annotation;

import io.github.zhenbing.frpc.config.*;

/**
 * FrpcConfigConfiguration
 *
 * @author fengzhenbing
 */
@EnableFrpcConfigBindings({
        @EnableFrpcConfigBinding(prefix = ApplicationConfig.FRPC +"."+ApplicationConfig.PREFIX, type = ApplicationConfig.class),
        @EnableFrpcConfigBinding(prefix = RegistryConfig.FRPC +"."+RegistryConfig.PREFIX, type = RegistryConfig.class),
        @EnableFrpcConfigBinding(prefix = ProviderConfig.FRPC +"."+ProviderConfig.PREFIX, type = ProviderConfig.class),
        @EnableFrpcConfigBinding(prefix = ConsumerConfig.FRPC +"."+ConsumerConfig.PREFIX, type = ConsumerConfig.class),
        @EnableFrpcConfigBinding(prefix = ConfigCenterConfig.FRPC +"."+ConfigCenterConfig.PREFIX, type = ConfigCenterConfig.class)
})
public class FrpcConfigConfiguration {
}
