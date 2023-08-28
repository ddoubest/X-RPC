package org.chike.rpc.core.spring;

import lombok.SneakyThrows;
import org.chike.rpc.core.annotation.Consumer;
import org.chike.rpc.core.proxy.ConsumerProxyGenerator;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;


public class ConsumerBeanPostProcessor implements BeanPostProcessor {
    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization(Object bean, @Nullable String beanName) {
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(Consumer.class)) {
                Consumer consumerAnnotation = declaredField.getAnnotation(Consumer.class);
                ConsumerProxyGenerator proxyGenerator = new ConsumerProxyGenerator(consumerAnnotation.group(), consumerAnnotation.version());
                Object consumerProxy = proxyGenerator.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                declaredField.set(bean, consumerProxy);
            }
        }
        return bean;
    }
}
