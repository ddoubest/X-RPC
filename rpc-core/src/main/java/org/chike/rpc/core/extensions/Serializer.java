package org.chike.rpc.core.extensions;

import org.chike.rpc.core.annotation.SPI;
import org.chike.rpc.core.codec.SelfEncode;

@SPI
public interface Serializer extends SelfEncode {
    byte[] serialize(Object source);
    Object deserialize(byte[] serialized);
}
