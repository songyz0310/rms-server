package org.geekpower.common.enums;

public enum Deleted {

    NO((byte) 0), // 0，未删除
    IS((byte) 1), // 1，已删除（回收站）
    REAL((byte) 2),// 2，已彻底删除
    ;

    private byte code;

    private Deleted(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

}
