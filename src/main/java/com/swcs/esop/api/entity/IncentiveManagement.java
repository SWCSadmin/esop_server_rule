package com.swcs.esop.api.entity;

import com.swcs.esop.api.common.base.ExcelUploadEntity;
import com.swcs.esop.api.module.excel.ExcelCheckEnum;
import com.swcs.esop.api.module.excel.annotion.ExcelCheckField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 阮程
 * @date 2022/11/1
 */

@Data
public class IncentiveManagement extends ExcelUploadEntity {

    @ExcelCheckField(ExcelCheckEnum.NotEmpty)
    private String schedule_batch_id;
    @ExcelCheckField(ExcelCheckEnum.NotEmpty)
    private String participant_id;
    @ExcelCheckField(value = ExcelCheckEnum.Number, re = "^([1-9]|1[0-9]|2[0-1])$")
    private String incentive_status;
    @ExcelCheckField(ExcelCheckEnum.Number)
    private String granted;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty, ExcelCheckEnum.Number})
    private String total_lapsed;

    @Override
    public String getPrimaryKey() {
        return schedule_batch_id + participant_id;
    }

    @Override
    public boolean primaryKeyIsBlank() {
        return StringUtils.isBlank(schedule_batch_id) || StringUtils.isBlank(participant_id);
    }
}
