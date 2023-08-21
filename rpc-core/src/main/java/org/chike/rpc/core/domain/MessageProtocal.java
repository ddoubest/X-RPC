package org.chike.rpc.core.domain;

import org.chike.rpc.core.factory.SingletonFactory;

public class MessageProtocal<T> {
    // // 1字节 魔数
    public static final MagicNumer MAGIC_NUMBER = SingletonFactory.getInstance(MagicNumer.class);
    // // 1字节 压缩方式
    // private CompressEnum compressEnum;
    // // 1字节 序列化方式
    // private SerialEnum serialEnum;
    // // 1字节 消息类型
    // private Message
    // // 4字节 消息Id
    // // 4字节 消息体的大小
    // // 消息体
}
