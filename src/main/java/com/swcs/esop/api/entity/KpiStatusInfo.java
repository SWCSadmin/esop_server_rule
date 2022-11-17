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
public class KpiStatusInfo extends ExcelUploadEntity {

    @ExcelCheckField(ExcelCheckEnum.NotEmpty)
    private String schedule_batch_id;
    @ExcelCheckField(ExcelCheckEnum.NotEmpty)
    private String participant_id;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty, ExcelCheckEnum.Number}, re = "^[12]$")
    private String kpi_no;
    @ExcelCheckField(ExcelCheckEnum.Number)
    private String kpi_status;
    private String remarks_en;
    private String remarks_sc;
    private String remarks_tc;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        KpiStatusInfo that = (KpiStatusInfo) o;
        return Objects.equals(schedule_batch_id, that.schedule_batch_id) &&
                Objects.equals(participant_id, that.participant_id) &&
                Objects.equals(kpi_no, that.kpi_no);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), schedule_batch_id, participant_id, kpi_no);
    }

    @Override
    public String getPrimaryKey() {
        return schedule_batch_id + participant_id + kpi_no;
    }

    @Override
    public boolean primaryKeyIsBlank() {
        return StringUtils.isBlank(schedule_batch_id) || StringUtils.isBlank(participant_id) || StringUtils.isBlank(kpi_no);
    }
}
