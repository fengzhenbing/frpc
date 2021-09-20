package io.github.zhenbing.frpc.core.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author fengzhenbing
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FrpcComponentScanRegistrar.class)
public @interface FrpcComponentScan {
}
