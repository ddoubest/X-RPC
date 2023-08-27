package org.chike.rpc.core.factory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.chike.rpc.core.annotation.SPI;
import org.chike.rpc.core.codec.NeedId;
import org.chike.rpc.core.config.RpcConfig;
import org.chike.rpc.core.enums.RpcConfigEnum;
import org.chike.rpc.core.exceptions.ExtensionClashException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class ExtensionLoader<T> {
    public static final String PATH = "META-INF/XRPC/extensions/";

    private static final Map<String, ExtensionLoader<?>> LOADERS = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Extension type should not be null.");
        }
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface.");
        }
        if (clazz.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }

        String clazzName = clazz.toString();
        if (!LOADERS.containsKey(clazzName)) {
            LOADERS.putIfAbsent(clazzName, new ExtensionLoader<S>(clazz));
        }
        return (ExtensionLoader<S>) LOADERS.get(clazzName);
    }

    private final Class<T> baseClazz;
    private final Map<String, T> instanceHolder = new ConcurrentHashMap<>();
    private final Map<String, Class<? extends T>> clazzHolder = new HashMap<>();

    private ExtensionLoader(Class<T> baseClazz) {
        this.baseClazz = baseClazz;
        loadAllExtensions();
    }

    private void loadAllExtensions() {
        String fileName = ExtensionLoader.PATH + baseClazz.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(resourceUrl);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadResource(URL resourceUrl) {
        ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // get index of comment
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    // string after # is comment so we ignore it
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    try {
                        final int ei = line.indexOf('=');
                        String name = line.substring(0, ei).trim();
                        String clazzName = line.substring(ei + 1).trim();
                        // our SPI use key-value pair so both of them must not be empty
                        if (name.length() > 0 && clazzName.length() > 0) {
                            Class<? extends T> clazz = (Class<? extends T>) classLoader.loadClass(clazzName);
                            if (clazzHolder.containsKey(name)
                                    && !clazzHolder.get(name).toString().equals(clazz.toString())) {
                                throw new ExtensionClashException(clazz.toString() + "包含多个name");
                            }
                            clazzHolder.put(name, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public T getInstance(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }
        if (!instanceHolder.containsKey(name)) {
            Class<? extends T> clazz = clazzHolder.get(name);
            if (clazz == null) {
                throw new RuntimeException("No such extension of name " + name);
            }
            try {
                instanceHolder.putIfAbsent(name, clazz.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return instanceHolder.get(name);
    }


    public T getInstanceById(Byte id) {
        List<T> result = new ArrayList<>();
        for (String key : clazzHolder.keySet()) {
            T instance = getInstance(key);
            if (instance instanceof NeedId) {
                byte componentId = ((NeedId) instance).getId();
                if (componentId == id) {
                    result.add(instance);
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        if (result.size() > 1) {
            String msg = "Extensions组件ID有冲突，冲突组件如下：\n" +
                    result.toString();
            throw new ExtensionClashException(msg);
        }

        return result.get(0);
    }

    public static <T> T getExtensionFromConfig(Class<T> clazz, RpcConfigEnum rpcConfigEnum) {
        return ExtensionLoader
                .getExtensionLoader(clazz)
                .getInstance(RpcConfig.getProperty(
                        rpcConfigEnum.getPropertyValue(),
                        rpcConfigEnum.getDefaultValue()));
    }
}
