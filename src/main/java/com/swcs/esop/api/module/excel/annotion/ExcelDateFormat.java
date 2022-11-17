package com.swcs.esop.api.module.excel.annotion;


import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelDateFormat {

    String value() default "yyyy-MM-dd HH:mm:ss";

    String original();

}
