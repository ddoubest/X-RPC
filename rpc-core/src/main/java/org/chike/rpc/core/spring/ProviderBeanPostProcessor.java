package org.chike.rpc.core.spring;

import org.chike.rpc.core.annotation.Provider;
import org.chike.rpc.core.domain.ProviderConfig;
import org.chike.rpc.core.domain.ServiceProvider;
import org.chike.rpc.core.factory.SingletonFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;


public class ProviderBeanPostProcessor implements BeanPostProcessor {
    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProvider.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, @Nullable String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Provider.class)) {
            Provider providerAnnotation = bean.getClass().getAnnotation(Provider.class);
            ProviderConfig providerConfig = ProviderConfig.builder()
                    .group(providerAnnotation.group())
                    .version(providerAnnotation.version())
                    .provider(bean)
                    .build();
            serviceProvider.publishProvider(providerConfig);
        }
        return bean;
    }
}
