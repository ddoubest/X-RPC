package org.chike.rpc.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.chike.rpc.core.codec.SelfEncode;

@AllArgsConstructor
@Getter
public enum MessageType implements SelfEncode {
    PING(0, "ping", "心跳请求"),
    PONG(1, "pong", "心跳响应"),
    RPC_REQ(2, "rpcReq", "RPC请求"),
    RPC_RESP(3, "rpcResp", "RPC响应");

    public static MessageType getMessageTypeById(byte id) {
        for (MessageType messageType : MessageType.values()) {
            if (id == messageType.id) {
                return messageType;
            }
        }
        return null;
    }

    private final Byte id;
    private final String name;
    private final String desc;

    MessageType(int id, String name, String desc) {
        this.id = (byte) id;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public byte[] encode() {
        return new byte[] {id};
    }
}
