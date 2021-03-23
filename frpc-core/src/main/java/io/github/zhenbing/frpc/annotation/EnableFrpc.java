package io.github.zhenbing.frpc.annotation;

import java.lang.annotation.*;

/**
 * EnableFrpc
 *
 * @author fengzhenbing
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@EnableFrpcConfig
@FrpcComponentScan
public @interface EnableFrpc {
}
