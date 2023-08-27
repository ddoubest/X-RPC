package org.chike.rpc.core.constant;

public class ProtocalConstants {
    // 协议的魔数
    public static final byte MAGIC_NUMBER = (byte) 0b01101001;
    // 协议中，header的字节数
    public static final int HEADER_SIZE = 8;
    // 协议中，长度域的偏移量
    public static final int LENGTH_FIELD_OFFSET = 4;
    // 协议中，长度域的字节数
    public static final int LENGTH_FIELD_SIZE = 4;
}
