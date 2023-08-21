package org.chike.rpc.core.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MagicNumer {
    private final byte magicNumber = (byte) 0b01101001; // 十进制: 105

    public boolean check(byte num) {
        return magicNumber == num;
    }
}
