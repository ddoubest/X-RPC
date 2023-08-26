package org.chike.rpc.core.extensions;

import org.chike.rpc.core.annotation.SPI;
import org.chike.rpc.core.domain.content.RpcRequest;

import java.util.List;

@SPI
public interface LoadBalance {
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
