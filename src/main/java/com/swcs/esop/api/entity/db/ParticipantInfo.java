package com.swcs.esop.api.entity.db;

import lombok.Data;

import java.util.Date;

/**
 * @author 阮程
 * @date 2022/11/8
 */
@Data
public class ParticipantInfo {

    private String participant_id;
    private String login_id;
    private String hk_id;
    private String passport_id;
    private String nationality;
    private String tax_residence;
    private Date employ_date;
    private Date depart_date;
    private Date mortality_date;
    private String prefer_name_en;
    private String prefer_name_sc;
    private String prefer_name_tc;
    private String first_name_en;
    private String first_name_sc;
    private String first_name_tc;
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
    private String job_position_sc;
    private String job_position_tc;
    private Date dob;
    private String participant_status;
    private String kyc_status;
    private String kyc_risk_level;
    private String department_en;
    private String department_sc;
    private String department_tc;
    private Date last_kyc_review_date;
    private String warning_type;
    private String kyc_remarks_en;
    private String kyc_remarks_sc;
    private String kyc_remarks_tc;
    private String support_docs_path;
    private Date next_kyc_review_date;
    private String reject_justification;
    private String Wealth_source_en;
    private String wealth_source_sc;
    private String wealth_source_tc;
    private String funds_source_en;
    private String funds_source_sc;
    private String funds_source_tc;
    private String reminder_count;
    private String vendor_id;

}
