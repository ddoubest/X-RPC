package org.chike.rpc.core.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface Provider {
    @AliasFor(annotation = Component.class)
    String value() default "";

    String group() default "";
    String version() default "";
}
