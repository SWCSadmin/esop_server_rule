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
 * @date 2022/12/13
 */
@Data
public class KycParticipantInfo extends ExcelUploadEntity {

    private String participant_id;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty, ExcelCheckEnum.Number}, re = "^[12345]$")
    private String kyc_status;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty, ExcelCheckEnum.Number}, re = "^[123]$")
    private String kyc_risk_level;
    private String warning_type;
    private String kyc_remarks_en;
    private String kyc_remarks_sc;
    private String kyc_remarks_tc;
    private String reject_justification;
    private String wealth_source_en;
    private String wealth_source_sc;
    private String wealth_source_tc;
    private String funds_source_en;
    private String funds_source_sc;
    private String funds_source_tc;

    @ExcelIgnore
    @ExcelDbUpdateField
    private String last_kyc_review_date;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String support_docs_path;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String next_kyc_review_date;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String reminder_count;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String hk_id;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String passport_id;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String nationality;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String tax_residence;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String employ_date;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String depart_date;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String mortality_date;
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
    private String first_name_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String first_name_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String first_name_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String last_name_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String last_name_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String last_name_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String telephone;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String mobile;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String email;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String residential_address_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String residential_address_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String residential_address_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String wechat;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String whatsapp;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String job_position_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String job_position_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String job_position_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String company_id;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String hk_id_country;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String passport_country;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String prc_id;
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
    private String company_watch_list;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String trustee_watch_list;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String participant_type;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String prc_gov_approval;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String gender_type;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String avatar_type;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String dob;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String participant_status;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String department_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String department_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String department_tc;

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
