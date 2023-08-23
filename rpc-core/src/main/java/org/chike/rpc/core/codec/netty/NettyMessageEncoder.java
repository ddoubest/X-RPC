package org.chike.rpc.core.codec.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang3.ArrayUtils;
import org.chike.rpc.core.codec.MessageEncoder;
import org.chike.rpc.core.domain.Message;


@ChannelHandler.Sharable
public class NettyMessageEncoder extends MessageToByteEncoder<Message> implements MessageEncoder {
    @Override
    public byte[] encode(Message msg) {
        return msg.encode();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        byte[] encodedMsg = encode(msg);
        if (ArrayUtils.isEmpty(encodedMsg)) {
            throw new RuntimeException("消息编码失败");
        }
        out.writeBytes(encodedMsg);
    }
}
