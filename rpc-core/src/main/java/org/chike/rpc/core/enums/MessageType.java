package org.chike.rpc.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageType {
    PING(0, "ping", "心跳请求"),
    PONG(1, "pong", "心跳响应"),
    RPC_REQ(2, "rpcReq", "RPC请求"),
    RPC_RESP(3, "rpcResp", "RPC响应");

    private final Byte id;
    private final String name;
    private final String desc;

    MessageType(int id, String name, String desc) {
        this.id = (byte) id;
        this.name = name;
        this.desc = desc;
    }
}
