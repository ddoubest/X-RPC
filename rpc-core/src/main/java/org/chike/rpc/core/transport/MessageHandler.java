package org.chike.rpc.core.transport;

import org.chike.rpc.core.domain.Message;

public interface MessageHandler {
    /**
     * 对请求消息处理
     * @param reqMsg request message
     */
    void handle(Message reqMsg);
}
