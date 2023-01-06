package com.swcs.esop.api.entity;

import com.swcs.esop.api.module.excel.ExcelCheckEnum;
import com.swcs.esop.api.module.excel.annotion.ExcelCheckField;
import lombok.Data;

/**
 * @author 阮程
 * @date 2022/12/13
 */
@Data
public class ParticipantInfoVendor extends ParticipantInfo {

    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    private String user_input_id_z;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    private String user_input_id_x;

    private String participant_id;
    private String vendor_id;

}
