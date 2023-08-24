package org.chike.rpc.core.extensions;

import org.chike.rpc.core.annotation.SPI;
import org.chike.rpc.core.codec.NeedId;
import org.chike.rpc.core.codec.SelfEncode;

@SPI
public interface Compresser extends NeedId, SelfEncode {
    byte[] compress(byte[] source);
    byte[] decompress(byte[] compressed);
}
