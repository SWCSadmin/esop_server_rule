package com.swcs.esop.api.entity;

import lombok.Data;

/**
 * @author 阮程
 * @date 2022/11/1
 */
@Data
public class KpiStatusInfo {

    private String schedule_batch_id;
    private String participant_id;
    private String kpi_no;
    private String kpi_status;
    private String remarks_en;
    private String remarks_sc;
    private String remarks_tc;

    private String error;
}
