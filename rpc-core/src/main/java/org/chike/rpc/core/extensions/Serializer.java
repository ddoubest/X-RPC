package org.chike.rpc.core.extensions;

import org.chike.rpc.core.annotation.SPI;
import org.chike.rpc.core.codec.NeedId;
import org.chike.rpc.core.codec.SelfEncode;

@SPI
public interface Serializer extends NeedId, SelfEncode {
    byte[] serialize(Object source);
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
