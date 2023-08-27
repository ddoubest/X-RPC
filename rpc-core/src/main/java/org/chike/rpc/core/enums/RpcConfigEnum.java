package org.chike.rpc.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcConfigEnum {
    REGISTRY_CENTER_NAME("xrpc.registry.name", "zk"),
    ZK_ADDRESS("xrpc.zookeeper.address", "127.0.0.1:2181"),
    LOAD_BALANCE_NAME("xrpc.load_balance.name", "consistentHash")
    ;

    private final String propertyValue;
    private final String defaultValue;
}