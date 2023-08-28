package org.chike.rpc.core.domain;

import lombok.Getter;
import lombok.ToString;
import org.chike.rpc.core.codec.SelfEncode;
import org.chike.rpc.core.constant.ProtocalConstants;

@Getter
@ToString
public class MagicNumer implements SelfEncode {
    private final byte magicNumber = ProtocalConstants.MAGIC_NUMBER; // 十进制: 105

    public boolean check(byte num) {
        return magicNumber == num;
    }

    @Override
    public byte[] encode() {
        return new byte[] {magicNumber};
    }
}
