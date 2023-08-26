package org.chike.rpc.core.extensions.impl;

import org.chike.rpc.core.domain.content.RpcRequest;
import org.chike.rpc.core.extensions.LoadBalance;
import org.springframework.util.CollectionUtils;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest) {
        if (CollectionUtils.isEmpty(serviceAddresses)) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses, rpcRequest);
    }

    protected abstract String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest);
}
