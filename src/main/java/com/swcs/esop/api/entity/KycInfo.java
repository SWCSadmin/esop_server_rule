package com.swcs.esop.api.entity;

import com.swcs.esop.api.common.base.ExcelUploadEntity;
import com.swcs.esop.api.module.excel.annotion.ExcelCheckField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 阮程
 * @date 2022/11/8
 */
@Data
public class KycInfo extends ExcelUploadEntity {

    @ExcelCheckField
    private String schedule_batch_id;
    private String total_watch_count;
    private String total_suspended_count;
    private String last_updated_date;
    private String en_desc;
    private String cn_desc;
    private String tw_desc;

    @Override
    public String getPrimaryKey() {
        return schedule_batch_id;
    }

    @Override
    public boolean primaryKeyIsBlank() {
        return StringUtils.isBlank(schedule_batch_id);
    }
}
