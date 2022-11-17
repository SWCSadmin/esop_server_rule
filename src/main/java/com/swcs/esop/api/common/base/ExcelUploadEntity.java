package com.swcs.esop.api.common.base;

import lombok.Data;

/**
 * @author 阮程
 * @date 2022/11/8
 */
@Data
public abstract class ExcelUploadEntity {

    protected String error;
    protected String status;

    public abstract String getPrimaryKey();

    public abstract boolean primaryKeyIsBlank();
}
