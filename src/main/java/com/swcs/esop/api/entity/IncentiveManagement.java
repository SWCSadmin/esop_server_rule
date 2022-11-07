package com.swcs.esop.api.entity;

import lombok.Data;

/**
 * @author 阮程
 * @date 2022/11/1
 */

@Data
public class IncentiveManagement {

    private String schedule_batch_id;
    private String participant_id;
    private String incentive_status;
    private Integer granted;
    private Integer total_lapsed;

    private String error;

}
