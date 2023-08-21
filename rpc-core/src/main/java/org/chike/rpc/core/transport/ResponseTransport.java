package org.chike.rpc.core.transport;

import org.chike.rpc.core.domain.Message;

public interface ResponseTransport {
    /**
     * 响应消息
     * @param respMsg response message
     * @return 响应是否发送成功
     */
    boolean response(Message respMsg);
}
