package org.geekpower.common.enums;

import java.util.Locale;
import java.util.Objects;

public enum Language {

    CHINESE("zh", Locale.CHINESE), //
    ENGLISH("en", Locale.ENGLISH),//
    ;

    private String code;
    private Locale locale;

    private Language(String code, Locale locale) {
        this.code = code;
        this.locale = locale;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public static Language get(String langCode) {
        if (Objects.isNull(langCode))
            return CHINESE;

        switch (langCode) {
            case "zh":
                return CHINESE;
            case "en":
                return ENGLISH;

            default:
                return CHINESE;
        }
    }

}
