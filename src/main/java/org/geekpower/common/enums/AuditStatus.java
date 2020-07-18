package org.geekpower.common.enums;

public enum AuditStatus {

    PASS((byte) 1), // 1，已通过，
    REJECT((byte) 2),// 2,已驳回

    ;

    private byte code;

    private AuditStatus(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

}
