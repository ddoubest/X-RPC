package org.chike.rpc.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    RPC_CONFIG_PATH("xrpc.properties"),
    REGISTRY_CENTER_NAME("xrpc.registry.name"),
    ZK_ADDRESS("xrpc.zookeeper.address"),
    LOAD_BALANCE_NAME("xrpc.load_balance.name")
    ;

    private final String propertyValue;

}