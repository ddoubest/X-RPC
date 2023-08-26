package org.chike.rpc.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseMessageEnum {
    REQ_MESSAGE_NOT_CONTAIN_RPC_REQUEST_INSTANCE("RPC请求不包含RpcRequest对象"),
    REQUEST_PROVIDER_NOT_EXIST("请求的Provider不存在")
    ;

    private final String message;
}