package org.chike.rpc.core.config;

import org.chike.rpc.core.enums.RpcConfigEnum;
import org.chike.rpc.core.utils.PropertiesFileUtil;

import java.util.Optional;
import java.util.Properties;

public class RpcConfig {
    private static final Properties properties =
            PropertiesFileUtil.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());

    public static String getProperty(String key) {
        return Optional.ofNullable(properties)
                .map(p -> p.getProperty(key))
                .orElse(null);
    }

    public static String getProperty(String key, String defaultValue) {
        return Optional.ofNullable(properties)
                .map(p -> p.getProperty(key))
                .orElse(defaultValue);
    }
}
