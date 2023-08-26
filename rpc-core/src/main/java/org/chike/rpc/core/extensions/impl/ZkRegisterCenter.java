package org.chike.rpc.core.extensions.impl;

import org.chike.rpc.core.config.RpcConfig;
import org.chike.rpc.core.constant.RpcComponentConstants;
import org.chike.rpc.core.domain.content.RpcRequest;
import org.chike.rpc.core.enums.RpcConfigEnum;
import org.chike.rpc.core.enums.RpcRuntimeErrorMessageEnum;
import org.chike.rpc.core.exceptions.RpcRuntimeException;
import org.chike.rpc.core.extensions.LoadBalance;
import org.chike.rpc.core.extensions.RegistryCenter;
import org.chike.rpc.core.factory.ExtensionLoader;
import org.chike.rpc.core.utils.ZkUtil;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;

public class ZkRegisterCenter implements RegistryCenter {
    private final LoadBalance loadBalance = ExtensionLoader
            .getExtensionLoader(LoadBalance.class)
            .getInstance(RpcConfig.getProperty(
                            RpcConfigEnum.LOAD_BALANCE_NAME.getPropertyValue(),
                            RpcComponentConstants.DEFAULT_LOAD_BALANCE));

    @Override
    public boolean registerService(String serviceName, InetSocketAddress address) {
        // InetSocketAddress.toString() 自带 "/"，eg: "/127.0.0.1:9899"
        String path = ZkUtil.ZK_REGISTER_ROOT_PATH + "/" + serviceName + address.toString();
        return ZkUtil.createPersistentNode(path);
    }

    @Override
    public InetSocketAddress discoverService(RpcRequest rpcRequest) {
        String path = ZkUtil.ZK_REGISTER_ROOT_PATH + "/" + rpcRequest.getRpcServiceName();
        List<String> serviceUrlList = ZkUtil.getChildrenNodes(path);
        if (CollectionUtils.isEmpty(serviceUrlList)) {
            throw new RpcRuntimeException(RpcRuntimeErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcRequest.getRpcServiceName());
        }
        String serviceAddress = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        String[] socketAddressArray = serviceAddress.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
