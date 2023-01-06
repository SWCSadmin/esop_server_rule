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
public class VendorInfo extends ExcelUploadEntity {

    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    private String user_input_id_z;
    private String vendor_id;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    private String vendor_name_en;
    private String vendor_name_sc;
    private String vendor_name_tc;
    private String business_reg_no;
    private String cert_incorp_no;
    private String tax_identification_no;
    private String industry_type;
    private String vendor_email;
    private String vendor_phone;
    private String place_incorp;
    private String place_tax_resident;
    private String registered_address_en;
    private String registered_address_sc;
    private String registered_address_tc;
    private String postal_address_en;
    private String postal_address_sc;
    private String postal_address_tc;
    private String vendor_director_name_en;
    private String vendor_director_name_sc;
    private String vendor_director_name_tc;
    private String vendor_shareholder_en;
    private String vendor_shareholder_sc;
    private String vendor_shareholder_tc;
    private String vendor_logo;

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
    private String kyc_status;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String kyc_risk_rating;
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
    private String funds_source_en;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String funds_source_sc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String funds_source_tc;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String last_kyc_review_date;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String warning_type;
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
    private String company_type;
    @ExcelIgnore
    @ExcelDbUpdateField
    private String reminder_count;

    @Override
    public String getPrimaryKey() {
        return vendor_id;
    }

    @Override
    public boolean primaryKeyIsBlank() {
        return StringUtils.isBlank(vendor_id);
    }
}
