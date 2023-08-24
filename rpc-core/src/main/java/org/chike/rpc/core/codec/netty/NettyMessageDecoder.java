package org.chike.rpc.core.codec.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.commons.lang3.ArrayUtils;
import org.chike.rpc.core.codec.ContentEncode;
import org.chike.rpc.core.codec.MessageDecoder;
import org.chike.rpc.core.constant.NettyConstants;
import org.chike.rpc.core.constant.ProtocalConstants;
import org.chike.rpc.core.domain.Message;
import org.chike.rpc.core.enums.MessageType;
import org.chike.rpc.core.extensions.Compresser;
import org.chike.rpc.core.extensions.Serializer;
import org.chike.rpc.core.factory.ExtensionLoader;

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
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(bytes.length);;
        try {
            byteBuf.writeBytes(bytes);
            return decode(byteBuf);
        } finally {
            byteBuf.release();
        }
    }

    private Message decode(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() < ProtocalConstants.HEADER_SIZE) {
            return null;
        }
        if (!Message.MAGIC_NUMBER.check(byteBuf.readByte())) {
            return null;
        }

        Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getInstanceById(byteBuf.readByte());
        if (serializer == null) {
            return null;
        }

        Compresser compresser = ExtensionLoader.getExtensionLoader(Compresser.class).getInstanceById(byteBuf.readByte());
        if (compresser == null) {
            return null;
        }

        MessageType messageType = MessageType.getMessageTypeById(byteBuf.readByte());
        if (messageType == null) {
            return null;
        }

        int messageId = byteBuf.readInt();
        int messageSize = byteBuf.readInt();

        ContentEncode content = null;
        if (messageSize > 0) {
            byte[] source = new byte[messageSize];
            byteBuf.readBytes(source);
            byte[] decompressd = compresser.decompress(source);
            content = serializer.deserialize(decompressd, ContentEncode.class);
        }

        return new Message(serializer, compresser, messageType, messageId, messageSize, content);
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
