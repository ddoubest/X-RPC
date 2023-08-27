package org.chike.rpc.core.config;

import org.chike.rpc.core.constant.RpcConstants;
import org.chike.rpc.core.utils.PropertiesFileUtil;

import java.util.Optional;
import java.util.Properties;

public class RpcConfig {
    private static final Properties properties =
            PropertiesFileUtil.readPropertiesFile(RpcConstants.RPC_CONFIG_PATH);

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
