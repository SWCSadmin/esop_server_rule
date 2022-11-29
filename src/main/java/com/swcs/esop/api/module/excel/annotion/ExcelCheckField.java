package com.swcs.esop.api.module.excel.annotion;


import com.swcs.esop.api.module.excel.ExcelCheckEnum;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelCheckField {

    ExcelCheckEnum[] value();

    String re() default "";

}
