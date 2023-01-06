package com.swcs.esop.api.entity;

import com.swcs.esop.api.common.base.ExcelUploadEntity;
import com.swcs.esop.api.module.excel.ExcelCheckEnum;
import com.swcs.esop.api.module.excel.annotion.ExcelCheckField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 阮程
 * @date 2022/12/9
 */
@Data
public class ContactPerson extends ExcelUploadEntity {

    private String contact_id;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    private String entity_id;
    private String avatar_type;
    private String gender_type;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    private String first_name_en;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    private String last_name_en;
    private String first_name_sc;
    private String last_name_sc;
    private String first_name_tc;
    private String last_name_tc;
    private String prefer_name_en;
    private String prefer_name_sc;
    private String prefer_name_tc;
    private String mobile;
    private String email;
    private String wechat;
    private String whatsapp;
    private String company_phone;
    private String remarks_en;
    private String remarks_sc;
    private String remarks_tc;
    private String company_position_en;
    private String company_position_sc;
    private String company_position_tc;
    private String address_en;
    private String address_sc;
    private String address_tc;
    private String equity_interest_pct;

    @Override
    public String getPrimaryKey() {
        return contact_id;
    }

    @Override
    public boolean primaryKeyIsBlank() {
        return StringUtils.isBlank(contact_id);
    }
}
