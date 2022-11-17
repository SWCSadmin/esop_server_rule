package com.swcs.esop.api.entity;

import com.swcs.esop.api.common.base.ExcelUploadEntity;
import com.swcs.esop.api.module.excel.ExcelCheckEnum;
import com.swcs.esop.api.module.excel.annotion.ExcelCheckField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

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
    /**
     * 1-21
     */
    @ExcelCheckField(value = ExcelCheckEnum.Number, re = "^([1-9]|1[0-9]|2[0-1])$")
    private String incentive_status;
    @ExcelCheckField(ExcelCheckEnum.Number)
    private String granted;
    private String total_lapsed;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IncentiveManagement that = (IncentiveManagement) o;
        return Objects.equals(schedule_batch_id, that.schedule_batch_id) &&
                Objects.equals(participant_id, that.participant_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), schedule_batch_id, participant_id);
    }

    @Override
    public String getPrimaryKey() {
        return schedule_batch_id + participant_id;
    }

    @Override
    public boolean primaryKeyIsBlank() {
        return StringUtils.isBlank(schedule_batch_id) || StringUtils.isBlank(participant_id);
    }
}
