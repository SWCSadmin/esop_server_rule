package com.swcs.esop.api.module.excel;

import java.util.Locale;

/**
 * @author 阮程
 * @date 2022/11/23
 */
public enum LocaleEnum {

    US(Locale.US);

    private Locale locale;

    LocaleEnum(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

}
