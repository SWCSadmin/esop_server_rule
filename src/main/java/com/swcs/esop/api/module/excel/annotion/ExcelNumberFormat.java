package com.swcs.esop.api.module.excel.annotion;

import java.lang.annotation.*;

/**
 * @author 阮程
 * @date 2022/11/16
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelNumberFormat {
    String value();
}
