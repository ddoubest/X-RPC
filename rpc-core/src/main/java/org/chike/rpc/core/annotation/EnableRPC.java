package org.chike.rpc.core.annotation;

import org.chike.rpc.core.spring.RpcBeanSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(RpcBeanSelector.class)
public @interface EnableRPC {
    boolean startServer() default true;
}
