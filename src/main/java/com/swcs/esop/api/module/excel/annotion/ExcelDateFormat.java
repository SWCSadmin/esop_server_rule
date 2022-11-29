package com.swcs.esop.api.module.excel.annotion;


import com.swcs.esop.api.module.excel.LocaleEnum;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelDateFormat {

    String value() default "yyyy-MM-dd HH:mm:ss";

    String original();

    LocaleEnum locale() default LocaleEnum.US;

}
