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
 * @date 2022/12/20
 */
@Data
public class KycVendorInfo extends ExcelUploadEntity {
    private String vendor_id;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty, ExcelCheckEnum.Number}, re = "^[12345]$")
    private String kyc_status;
    private String kyc_risk_rating;
    private String funds_source_en;
    private String funds_source_sc;
    private String funds_source_tc;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty, ExcelCheckEnum.Number}, re = "^([1-9]|1[0-4])$")
    private String warning_type;
    private String kyc_remarks_en;
    private String kyc_remarks_sc;
    private String kyc_remarks_tc;
    private String reject_justification;
    private String company_type;

    @ExcelIgnore
    @ExcelDbUpdateField
    private String service_provided_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String service_provided_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String service_provided_tc;
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
    private String remarks_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String remarks_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String remarks_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String industry_type;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String vendor_email;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String vendor_phone;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String place_incorp;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String place_tax_resident;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String registered_address_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String registered_address_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String registered_address_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String postal_address_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String postal_address_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String postal_address_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String vendor_director_name_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String vendor_director_name_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String vendor_director_name_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String vendor_shareholder_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String vendor_shareholder_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String vendor_shareholder_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String vendor_logo;

    @Override
    public String getPrimaryKey() {
        return vendor_id;
    }

    @Override
    public boolean primaryKeyIsBlank() {
        return StringUtils.isBlank(vendor_id);
    }
}