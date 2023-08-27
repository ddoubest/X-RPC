package org.chike.rpc.core.domain;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chike.rpc.core.codec.ContentEncode;
import org.chike.rpc.core.codec.SelfEncode;
import org.chike.rpc.core.constant.ProtocalConstants;
import org.chike.rpc.core.enums.MessageType;
import org.chike.rpc.core.extensions.Compresser;
import org.chike.rpc.core.extensions.Serializer;
import org.chike.rpc.core.factory.SingletonFactory;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message implements SelfEncode {
    // 1字节 魔数
    public static final MagicNumer MAGIC_NUMBER = SingletonFactory.getInstance(MagicNumer.class);
    // 1字节 序列化方式
    private Serializer serializer;
    // 1字节 压缩方式
    private Compresser compresser;
    // 1字节 消息类型
    private MessageType messageType;
    // 4字节 消息体的大小
    private Integer contentSize;
    // 消息体
    private ContentEncode content;

    @Override
    public byte[] encode() {
        // 参数校验
        if (!check()) return null;

        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(ProtocalConstants.HEADER_SIZE);
        try {
            byteBuf.writeBytes(MAGIC_NUMBER.encode())
                    .writeBytes(serializer.encode())
                    .writeBytes(compresser.encode())
                    .writeBytes(messageType.encode());

            if (content == null
                    || messageType == MessageType.PING
                    || messageType == MessageType.PONG) {
                byteBuf.writeInt(0);
                return ByteBufUtil.getBytes(byteBuf);
            }

            byte[] encodedContent = content.encode(serializer, compresser);
            byteBuf.writeInt(encodedContent.length);
            byteBuf.writeBytes(encodedContent);

            return ByteBufUtil.getBytes(byteBuf);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            byteBuf.release();
        }
    }

    private boolean check() {
        if (serializer == null || compresser == null || messageType == null) {
            return false;
        }

        return true;
    }
}
