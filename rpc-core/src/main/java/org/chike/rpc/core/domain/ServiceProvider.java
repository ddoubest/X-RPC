package org.chike.rpc.core.domain;

import lombok.extern.slf4j.Slf4j;
import org.chike.rpc.core.constant.NettyConstants;
import org.chike.rpc.core.enums.RpcConfigEnum;
import org.chike.rpc.core.enums.RpcRuntimeErrorMessageEnum;
import org.chike.rpc.core.exceptions.RpcRuntimeException;
import org.chike.rpc.core.extensions.RegistryCenter;
import org.chike.rpc.core.factory.ExtensionLoader;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServiceProvider {
    private final Map<String, Object> providers = new ConcurrentHashMap<>();
    private final RegistryCenter registryCenter = ExtensionLoader
            .getExtensionFromConfig(RegistryCenter.class, RpcConfigEnum.REGISTRY_CENTER);

    public Object getProvider(String key) {
        return providers.get(key);
    }

    public void addProvider(ProviderConfig providerConfig) {
        providers.putIfAbsent(providerConfig.getRpcServiceName(), providerConfig.getProvider());
    }

    public void publishProvider(ProviderConfig providerConfig) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();

            boolean success = registryCenter.registerService(
                    providerConfig.getRpcServiceName(),
                    new InetSocketAddress(host, NettyConstants.SERVER_PORT)
            );

            if (success) {
                this.addProvider(providerConfig);
            } else {
                throw new RpcRuntimeException(
                        RpcRuntimeErrorMessageEnum.PUBLISH_SERVICE_ERROR,
                        providerConfig.toString()
                );
            }

        } catch (UnknownHostException e) {
            log.error("Occur exception when getHostAddress: ", e);
        }
    }
}
