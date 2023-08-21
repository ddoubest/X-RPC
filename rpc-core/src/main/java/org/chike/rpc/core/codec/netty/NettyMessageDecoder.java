package org.chike.rpc.core.codec.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.commons.lang3.ArrayUtils;
import org.chike.rpc.core.codec.MessageDecoder;
import org.chike.rpc.core.constant.NettyConstants;
import org.chike.rpc.core.constant.ProtocalConstants;
import org.chike.rpc.core.domain.Message;

@ChannelHandler.Sharable
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder implements MessageDecoder {
    public NettyMessageDecoder() {
        super(NettyConstants.MAX_FRAME_LENGTH, ProtocalConstants.LENGTH_FIELD_OFFSET, ProtocalConstants.LENGTH_FIELD_SIZE);
    }

    @Override
    public Message decode(byte[] bytes) {
        if (ArrayUtils.isEmpty(bytes)) {
            throw new IllegalArgumentException("待解码byte数组为空");
        }
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(bytes.length);
        byteBuf.writeBytes(bytes);
        try {
            return decode(byteBuf);
        } finally {
            byteBuf.release();
        }
    }

    private Message decode(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() < ProtocalConstants.HEADER_SIZE) {
            return null;
        }
        return null;
    }


    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decodedObj = super.decode(ctx, in);
        if (decodedObj instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) decodedObj;
            try {
                Message msg = decode(byteBuf);
                if (msg == null) {
                    throw new RuntimeException("消息解码失败");
                }
                return msg;
            } finally {
                byteBuf.release();
            }
        }
        return decodedObj;
    }
}
