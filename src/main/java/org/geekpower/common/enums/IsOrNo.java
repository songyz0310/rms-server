package org.geekpower.common.enums;

public enum IsOrNo {

    NO((byte) 0), IS((byte) 1);

    private byte code;

    private IsOrNo(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

}
