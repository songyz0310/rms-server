package org.geekpower.common.enums;

public enum IsOrNo {
    NO((short) 0), IS((short) 1);

    private short code;

    private IsOrNo(short code) {
        this.code = code;
    }

    public short getCode() {
        return code;
    }

}
