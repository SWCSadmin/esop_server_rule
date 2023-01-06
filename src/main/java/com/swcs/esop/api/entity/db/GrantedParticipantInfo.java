package com.swcs.esop.api.entity.db;

import com.swcs.esop.api.enums.IncentiveStatus;
import lombok.Data;
import springfox.documentation.service.ApiListing;

/**
 * @author 阮程
 * @date 2022/12/27
 */
@Data
public class GrantedParticipantInfo {

    private String schedule_batch_id;
    private String participant_id;
    private String granted;
    private String incentive_status;
    private String first_name_en;
    private String last_name_en;
    private String first_name_sc;
    private String last_name_sc;
    private String first_name_tc;
    private String last_name_tc;
    private String participant_type;
    private String department_en;
    private String department_sc;
    private String department_tc;
    private String job_position_en;
    private String job_position_sc;
    private String job_position_tc;
    private String incentive_group_en;
    private String incentive_group_sc;
    private String incentive_group_tc;
    private String batch_no;
    private String plan_name_en;
    private String plan_name_sc;
    private String plan_name_tc;
    private String plan_status;
}
