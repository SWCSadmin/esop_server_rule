package com.swcs.esop.api.entity;

import lombok.Data;

/**
 * @author 阮程
 * @date 2022/12/20
 */
@Data
public class CompanyInfo {

    private String company_id;
    private String company_name_en;
    private String company_name_sc;
    private String company_email;
    private String company_phone;
    private String postal_address_en;
    private String postal_address_sc;
    private String registered_address_en;
    private String registered_address_sc;
    private String incorporation_date;
    private String financial_year_end;
    private String company_name_tc;
    private String postal_address_tc;
    private String registered_address_tc;
    private String tax_identification_no;
    private String business_reg_no;
    private String cert_incorp_no;
    private String logo;
    private String principal_address_en;
    private String principal_address_sc;
    private String principal_address_tc;
    private String industry_type;
    private String place_of_incorporation;

}
