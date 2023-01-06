package com.swcs.esop.api.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.swcs.esop.api.common.base.ExcelUploadEntity;
import com.swcs.esop.api.module.excel.ExcelCheckEnum;
import com.swcs.esop.api.module.excel.annotion.ExcelCheckField;
import com.swcs.esop.api.module.excel.annotion.ExcelDateFormat;
import com.swcs.esop.api.module.excel.annotion.ExcelDbUpdateField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 阮程
 * @date 2022/11/8
 */
@Data
public class ParticipantInfo extends ExcelUploadEntity {

    // 不加 @ExcelIgnore excel 解析的时候会默认有这个字段导致顺序错乱
    @ExcelIgnore
    private String participant_id;
    private String hk_id;
    private String passport_id;
    private String nationality;
    private String tax_residence;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    @ExcelDateFormat(value = "yyyy-MM-dd", original = "d-MMM-yy")
    private String employ_date;
    @ExcelDateFormat(value = "yyyy-MM-dd", original = "d-MMM-yy")
    private String depart_date;
    @ExcelDateFormat(value = "yyyy-MM-dd", original = "d-MMM-yy")
    private String mortality_date;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    private String prefer_name_en;
    private String prefer_name_sc;
    private String prefer_name_tc;
    private String first_name_en;
    private String first_name_sc;
    private String first_name_tc;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    private String last_name_en;
    private String last_name_sc;
    private String last_name_tc;
    private String telephone;
    private String mobile;
    private String email;
    private String residential_address_en;
    private String residential_address_sc;
    private String residential_address_tc;
    private String wechat;
    private String whatsapp;
    private String job_position_en;
    private String job_position_sc;
    private String job_position_tc;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    private String company_id;
    private String hk_id_country;
    private String passport_country;
    private String prc_id;
    private String remarks_en;
    private String remarks_sc;
    private String remarks_tc;
    private String company_watch_list;
    private String trustee_watch_list;
    private String participant_type;
    private String prc_gov_approval;
    private String gender_type;
    private String avatar_type;
    private String dob;
    private String participant_status;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty, ExcelCheckEnum.Number}, re = "^[12345]$")
    private String kyc_status;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty, ExcelCheckEnum.Number}, re = "^[123]$")
    private String kyc_risk_level;
    private String department_en;
    private String department_sc;
    private String department_tc;

    @ExcelIgnore
    @ExcelDbUpdateField
//    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty, ExcelCheckEnum.Number}, re = "^([1-9]|1[0-4])$")
    private String warning_type;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String last_kyc_review_date;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String kyc_remarks_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String kyc_remarks_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String kyc_remarks_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String support_docs_path;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String next_kyc_review_date;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String reject_justification;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String wealth_source_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String wealth_source_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String wealth_source_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String funds_source_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String funds_source_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String funds_source_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String reminder_count;

    @ExcelIgnore
    @ExcelDbUpdateField
    private String login_id;

    @Override
    public String getPrimaryKey() {
        return getParticipant_id();
    }

    @Override
    public boolean primaryKeyIsBlank() {
        return StringUtils.isBlank(getParticipant_id());
    }

}
