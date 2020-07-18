package org.geekpower.common.enums;

public enum ApplyStatus {

    PROGRESS((byte) 0), // 0，进行中，
    PASS((byte) 1), // 1，已通过，
    REJECT((byte) 2),// 2,已驳回

    ;

    private byte code;

    private ApplyStatus(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

}
