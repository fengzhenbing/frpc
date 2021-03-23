package io.github.zhenbing.frpc.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * EnableFrpcConfig
 *
 * @author fengzhenbing
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(FrpcConfigConfigurationRegistrar.class)
public @interface EnableFrpcConfig {
}
