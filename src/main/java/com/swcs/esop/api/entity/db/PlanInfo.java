package com.swcs.esop.api.entity.db;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 阮程
 * @date 2022/12/27
 */
@Data
public class PlanInfo {

    private Date start_date;
    private Date end_date;
    private Long issued_shares_capital;
    private Date adoption_date;
    private String plan_name_en;
    private String plan_id;
    private String company_id;
    private BigDecimal share_conversion_ratio;
    private String trust_id;
    private String supporting_docs;
    private String plan_status;
    private String plan_type;
    private String plan_sub_type;
    private String plan_name_sc;
    private String plan_name_tc;
    private BigDecimal contribution_share_pct;

}
