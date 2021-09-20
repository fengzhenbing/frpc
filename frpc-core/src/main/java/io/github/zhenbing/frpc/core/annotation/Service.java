package io.github.zhenbing.frpc.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Service annotation
 *
 * @author fengzhenbing
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Component
public @interface Service {
    /**
     * Interface class, default value is void.class
     */
    Class<?> interfaceClass() default void.class;

    /**
     * Interface class name, default value is empty string
     */
    String interfaceName() default "";

    /**
     * Service version, default value is empty string
     */
    String version() default "";
}
