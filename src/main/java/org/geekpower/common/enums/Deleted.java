package org.geekpower.common.enums;

public enum Deleted {
    NO((short) 0), // 0，未删除
    IS((short) 1), // 1，已删除（回收站）
    REAL((short) 2),// 2，已彻底删除
    ;

    private short code;

    private Deleted(short code) {
        this.code = code;
    }

    public short getCode() {
        return code;
    }

}
