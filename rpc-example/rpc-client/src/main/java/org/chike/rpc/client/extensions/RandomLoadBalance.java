package org.chike.rpc.client.extensions;

import org.chike.rpc.core.domain.content.RpcRequest;
import org.chike.rpc.core.extensions.impl.AbstractLoadBalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        int index = ThreadLocalRandom.current().nextInt(serviceAddresses.size());
        return serviceAddresses.get(index);
    }
}
