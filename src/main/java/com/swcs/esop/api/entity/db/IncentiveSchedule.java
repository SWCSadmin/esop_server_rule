package com.swcs.esop.api.entity.db;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 阮程
 * @date 2022/12/27
 */
@Data
public class IncentiveSchedule {

    private String schedule_batch_id;
    private String plan_id;
    private String incentive_group_en;
    private String incentive_group_sc;
    private String incentive_group_tc;
    private Date vesting_date;
    private Date exercise_start_date;
    private Date exercise_end_date;
    private BigDecimal grant_pct;
    private String broker_id;
    private String registrar_id;
    private Integer group_grantable_shares;
    private String securities_company;
    private Integer batch_no;
    private String offer_approver_id;
    private Date offer_date;
    private Date offer_expiry_date;
    private BigDecimal original_share_price;
    private BigDecimal available_funds;

}
