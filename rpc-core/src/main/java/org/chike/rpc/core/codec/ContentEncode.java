package org.chike.rpc.core.codec;

import org.chike.rpc.core.extensions.Compresser;
import org.chike.rpc.core.extensions.Serializer;

public interface ContentEncode {
    default byte[] encode(Serializer serializer, Compresser compresser) {
        byte[] serialized = serializer.serialize(this);
        return compresser.compress(serialized);
    }
}
