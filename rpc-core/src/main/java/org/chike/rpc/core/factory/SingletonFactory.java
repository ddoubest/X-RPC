package org.chike.rpc.core.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public final class SingletonFactory {
    private static final Map<String, Object> SINGLETONS = new ConcurrentHashMap<>();

    private SingletonFactory() {

    }

    public static <T> T getInstance(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("单例工厂不接受null入参");
        }
        String k = clazz.toString();
        if (!SINGLETONS.containsKey(k)) {
            return clazz.cast(SINGLETONS.computeIfAbsent(k, key -> {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException("反射生成单例对象失败", e);
                }
            }));
        }
        return clazz.cast(SINGLETONS.get(k));
    }
}
