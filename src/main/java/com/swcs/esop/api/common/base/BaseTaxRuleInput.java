package com.swcs.esop.api.common.base;

import com.swcs.esop.api.entity.tax.TaxRate;
import com.swcs.esop.api.util.AppUtils;
import com.swcs.esop.api.util.TaxRateUtil;

import java.math.BigDecimal;

/**
 * @author 阮程
 * @date 2022/11/8
 */
public interface BaseTaxRuleInput extends BaseRuleInput {


    default TaxRate findTaxRate(BigDecimal taxableIncome, boolean chinese) {
        TaxRateUtil taxRateUtil = AppUtils.getBean(TaxRateUtil.class);
        double d = taxableIncome.doubleValue();
        TaxRate[] taxRateArr;
        if (chinese) {
            taxRateArr = taxRateUtil.getTaxRate();
        } else {
            taxRateArr = taxRateUtil.getNonTaxRate();
        }
        for (TaxRate taxRate : taxRateArr) {
            BigDecimal min = taxRate.getMinTaxableIncome();
            BigDecimal max = taxRate.getMaxTaxableIncome();
            if (max == null) {
                max = new BigDecimal(Integer.MAX_VALUE);
            }
            double mind = min.doubleValue();
            if (mind == 0.0D && min.doubleValue() <= d && d <= max.doubleValue()) {
                return taxRate;
            } else if (min.doubleValue() < d && d <= max.doubleValue()) {
                return taxRate;
            }
        }
        throw new RuntimeException("No suitable tax rate is matched: " + taxableIncome.toString());
    }

    /**
     * 税率计算规则: 扣缴税额=计税收入*税率-速算扣除数
     *
     * @param taxableIncome
     * @return
     */
    default BigDecimal calTaxWithheld(BigDecimal taxableIncome, boolean chinese) {
        TaxRate taxRate = findTaxRate(taxableIncome, chinese);
        return taxableIncome.multiply(taxRate.getTaxRate()).subtract(taxRate.getQcd());
    }

}
