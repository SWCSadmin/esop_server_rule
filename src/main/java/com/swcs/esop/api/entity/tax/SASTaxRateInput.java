package com.swcs.esop.api.entity.tax;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 阮程
 * @date 2022/10/31
 */
@Data
public class SASTaxRateInput {

    /**
     * 是否中国公民
     */
    private boolean chinese = true;
    /**
     * 原始每股归属价格(Vested Price) VEP
     */
    private BigDecimal vep;
    /**
     * 股权数量
     */
    private BigDecimal noS;
    /**
     * 归属日每股价格MPVD
     */
    private BigDecimal mpvd;
    /**
     * 免税比例，默认为0，CP
     */
    private BigDecimal cp = new BigDecimal(0);
    /**
     * RSU 转变单元CU，默认为1
     */
    private BigDecimal cu = new BigDecimal(1);

}
