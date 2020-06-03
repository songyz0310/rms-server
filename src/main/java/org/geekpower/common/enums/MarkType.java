package org.geekpower.common.enums;

public enum MarkType {

    UN_READ((byte) 0), // 0，未读
    IS_READED((byte) 1), // 1，已读，
    UN_RUBBISH((byte) 2), // 2，不是垃圾
    IS_RUBBISH((byte) 3), // 3，是垃圾，
    ;

    private byte code;

    private MarkType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static MarkType get(byte code) {
        switch (code) {
            case 0:
                return UN_READ;
            case 1:
                return IS_READED;
            case 2:
                return UN_RUBBISH;
            case 3:
                return IS_RUBBISH;

            default:
                return IS_READED;
        }
    }

}
