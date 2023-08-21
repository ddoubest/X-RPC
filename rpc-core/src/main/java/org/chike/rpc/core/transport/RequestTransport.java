package org.chike.rpc.core.transport;

import org.chike.rpc.core.domain.Message;

public interface RequestTransport {
    /**
     * send message
     * @param reqMsg request message
     * @return response message
     */
    Message sendMessage(Message reqMsg);
}
