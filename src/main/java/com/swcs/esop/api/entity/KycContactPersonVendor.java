package com.swcs.esop.api.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.swcs.esop.api.common.base.ExcelUploadEntity;
import com.swcs.esop.api.module.excel.ExcelCheckEnum;
import com.swcs.esop.api.module.excel.annotion.ExcelCheckField;
import com.swcs.esop.api.module.excel.annotion.ExcelDbUpdateField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 阮程
 * @date 2022/12/9
 */
@Data
public class KycContactPersonVendor extends ExcelUploadEntity {

    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    private String contact_id;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    private String entity_id;
    private String equity_interest_pct;

    @ExcelIgnore
    @ExcelDbUpdateField
    private String avatar_type;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String gender_type;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String first_name_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String last_name_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String first_name_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String last_name_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String first_name_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String last_name_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String prefer_name_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String prefer_name_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String prefer_name_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String mobile;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String email;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String wechat;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String whatsapp;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String company_phone;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String remarks_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String remarks_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String remarks_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String company_position_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String company_position_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String company_position_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String address_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String address_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String address_tc;

    @Override
    public String getPrimaryKey() {
        return contact_id;
    }

    @Override
    public boolean primaryKeyIsBlank() {
        return StringUtils.isBlank(contact_id);
    }
}
