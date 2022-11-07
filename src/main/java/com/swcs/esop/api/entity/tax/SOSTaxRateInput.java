package com.swcs.esop.api.entity.tax;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 阮程
 * @date 2022/10/31
 */
@Data
public class SOSTaxRateInput {

    /**
     * 是否中国公民
     */
    private boolean chinese = true;
    /**
     * 原始每股行权价格OEP(Exercised Price)
     */
    private BigDecimal oep;
    /**
     * 股权数量
     */
    private BigDecimal noS;
    /**
     * 行权日每股价格MPED
     */
    private BigDecimal mped;

}
