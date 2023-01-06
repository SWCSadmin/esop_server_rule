package com.swcs.esop.api.entity;

import lombok.Data;

/**
 * @author 阮程
 * @date 2022/11/14
 */
@Data
public class ScheduleGroup {

    private String schedule_batch_id;
    private String schedule_group_id;
    private String incentive_group_en;
    private String incentive_group_sc;
    private String incentive_group_tc;

}
