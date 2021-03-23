package io.github.zhenbing.frpc.annotation;

import java.lang.annotation.*;

/**
 * EnableFrpcConfigBinding
 *
 * @author fengzhenbing
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(EnableFrpcConfigBindings.class)
//@Import(FrpcConfigBindingRegistrar.class)
public @interface EnableFrpcConfigBinding {
    String prefix();
    Class<?> type();
}
