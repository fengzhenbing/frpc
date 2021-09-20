package io.github.zhenbing.frpc.core.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * EnableRpxfxConfigBindings
 *
 * @author fengzhenbing
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FrpcConfigBindingsRegistrar.class)
public @interface EnableFrpcConfigBindings {
    /**
     * The value of {@link EnableFrpcConfigBindings}
     *
     * @return non-null
     */
    EnableFrpcConfigBinding[] value();
}
