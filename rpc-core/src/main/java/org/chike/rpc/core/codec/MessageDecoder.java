package org.chike.rpc.core.codec;

import org.chike.rpc.core.domain.Message;

public interface MessageDecoder {
    /**
     * 解码消息
     * @param bytes 解码前的byte数组
     * @return message对象
     */
    Message decode(byte[] bytes);
}
