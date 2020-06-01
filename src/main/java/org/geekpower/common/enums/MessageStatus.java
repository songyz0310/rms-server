package org.geekpower.common.enums;

public enum MessageStatus {

    DRAFT((byte) 0), // 0，草稿，
    FORMAL((byte) 1),// 1，已生效
    ;

    private byte code;

    private MessageStatus(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

}
