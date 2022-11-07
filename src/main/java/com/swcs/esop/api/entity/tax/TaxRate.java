package com.swcs.esop.api.entity.tax;

import com.swcs.esop.api.enums.TaxRateUnit;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 阮程
 * @date 2022/10/31
 */
@Data
public class TaxRate {

    /**
     * 最小计税收入
     */
    private BigDecimal minTaxableIncome;

    /**
     * 最大计税收入
     */
    private BigDecimal maxTaxableIncome;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 速算扣除数
     */
    private BigDecimal qcd;

    private TaxRateUnit unit;

}
