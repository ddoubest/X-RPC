package org.chike.rpc.core.codec;

import org.chike.rpc.core.domain.Message;

public interface MessageEncoder {
    /**
     * 编码消息
     * @param msg message对象
     * @return 编码后的byte数组
     */
    byte[] encode(Message msg);
}
