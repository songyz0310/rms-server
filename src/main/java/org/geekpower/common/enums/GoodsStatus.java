package org.geekpower.common.enums;

public enum GoodsStatus {

    CREATED((byte) 0), // 0，已创建，
    APPLYED((byte) 1), // 1，已申请，
    REJECT((byte) 2), // 2,已驳回，
    DEPLOY((byte) 3),// 3,已发布
    ;

    private byte code;

    private GoodsStatus(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

}
