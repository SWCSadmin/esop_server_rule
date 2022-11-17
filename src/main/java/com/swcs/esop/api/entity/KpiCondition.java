package com.swcs.esop.api.entity;

import lombok.Data;

/**
 * @author 阮程
 * @date 2022/11/14
 */
@Data
public class KpiCondition {

    private String schedule_batch_id;
    private String kpi_no;
    private String kpi_approver_id;
    private String fulfillment_date;
    private String pass_threshold_pct;
    private String kpi_desc_en;
    private String kpi_desc_sc;
    private String kpi_desc_tc;

}
