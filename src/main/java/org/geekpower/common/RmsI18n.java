package org.geekpower.common;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class RmsI18n {

    private static final Object[] empty = new Object[] {};
    private static final String baseName = "i18n.message";
    public static final String ERR_PREFIX = "err.code.";// 异常码前缀

    private static RmsI18n self = new RmsI18n();

    private RmsI18n() {
    }

    public static RmsI18n getInstance() {
        return self;
    }

    public String getMessage(String code) {
        return getMessage(Locale.CHINESE, code, empty);
    }

    public String getMessage(Locale lang, String code) {
        return getMessage(lang, code, empty);
    }

    public String getMessage(Locale lang, String code, Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, lang);
        return MessageFormat.format(bundle.getString(code), args);
    }

}
