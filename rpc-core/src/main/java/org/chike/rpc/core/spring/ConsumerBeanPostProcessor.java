package org.chike.rpc.core.spring;

import org.chike.rpc.core.annotation.Consumer;
import org.chike.rpc.core.proxy.ConsumerProxyGenerator;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;


public class ConsumerBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, @Nullable String beanName) {
        Class<?> clazz = bean.getClass();

        Arrays.stream(clazz.getDeclaredFields()).parallel().forEach(declaredField -> {
            if (declaredField.isAnnotationPresent(Consumer.class)) {
                Consumer consumerAnnotation = declaredField.getAnnotation(Consumer.class);
                ConsumerProxyGenerator proxyGenerator = new ConsumerProxyGenerator(consumerAnnotation.group(), consumerAnnotation.version());
                Object consumerProxy = proxyGenerator.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, consumerProxy);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Arrays.stream(clazz.getDeclaredMethods()).parallel().forEach(declaredMethod -> {
            if (declaredMethod.isAnnotationPresent(Consumer.class) && declaredMethod.getParameterCount() == 1) {
                Consumer consumerAnnotation = declaredMethod.getAnnotation(Consumer.class);
                ConsumerProxyGenerator proxyGenerator = new ConsumerProxyGenerator(consumerAnnotation.group(), consumerAnnotation.version());
                Object consumerProxy = proxyGenerator.getProxy(declaredMethod.getParameterTypes()[0]);
                declaredMethod.setAccessible(true);
                try {
                    declaredMethod.invoke(bean, consumerProxy);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return bean;
    }
}
