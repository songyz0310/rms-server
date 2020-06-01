package org.geekpower.common.enums;

public enum MessageStatus {
    DRAFT((short) 0), // 0，草稿，
    FORMAL((short) 1),// 1，已生效
    ;

    private short code;

    private MessageStatus(short code) {
        this.code = code;
    }

    public short getCode() {
        return code;
    }

}
