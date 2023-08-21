package org.chike.rpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chike.rpc.core.enums.CompressType;
import org.chike.rpc.core.enums.MessageType;
import org.chike.rpc.core.enums.SerialType;
import org.chike.rpc.core.factory.SingletonFactory;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    // 1字节 魔数
    public static final MagicNumer MAGIC_NUMBER = SingletonFactory.getInstance(MagicNumer.class);
    // 1字节 压缩方式
    private CompressType compressType;
    // 1字节 序列化方式
    private SerialType serialType;
    // 1字节 消息类型
    private MessageType messageType;
    // 4字节 消息Id
    private Integer messageId;
    // 4字节 消息体的大小
    private Integer contentSize;
    // 消息体
    private Object content;
}
