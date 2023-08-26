package org.chike.rpc.core.extensions;

import org.chike.rpc.core.annotation.SPI;
import org.chike.rpc.core.domain.content.RpcRequest;

import java.net.InetSocketAddress;

@SPI
public interface RegistryCenter {
    boolean registerService(String serviceName, InetSocketAddress address);
    InetSocketAddress discoverService(RpcRequest rpcRequest);
}
