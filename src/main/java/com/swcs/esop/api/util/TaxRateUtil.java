package com.swcs.esop.api.util;

import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.entity.tax.SASTaxRateInput;
import com.swcs.esop.api.entity.tax.SOSTaxRateInput;
import com.swcs.esop.api.entity.tax.TaxRate;
import com.swcs.esop.api.enums.Status;
import com.swcs.esop.api.enums.TaxRateUnit;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.math.BigDecimal;

/**
 * @author 阮程
 * @date 2022/10/31
 */
@Component
@ConfigurationProperties(prefix = "tax")
@Data
public class TaxRateUtil {

    private TaxRateUnit unit;
    private TaxRate[] taxRate;
    private TaxRate[] nonTaxRate;

    public static TaxRate findTaxRate(BigDecimal taxableIncome, boolean chinese) {
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
    public static BigDecimal calTaxWithheld(BigDecimal taxableIncome, boolean chinese) {
        TaxRate taxRate = findTaxRate(taxableIncome, chinese);
        return taxableIncome.multiply(taxRate.getTaxRate()).subtract(taxRate.getQcd());
    }

    /**
     * SOS预扣税服务
     * <p>
     *
     * <p>
     * 输入参数：
     * 1，	原始每股行权价格OEP(Exercised Price)
     * 2，	股权数量 NoS
     * 3，	行权日每股价格MPED
     * 需查询的参数： 税率Tax rate 和 速算扣除数 Quick deduction
     * 最后输出：Withholding tax的值 (第8步计算结果)
     * <p>
     * 预扣税额计算规则：
     * 1，	应纳税收=NoS x (MPED - OEP), 假如MPED <OEP应纳税收 = NoS x OEP
     * 2，	根据应纳税收额从表<二>中找到对应税率和速算扣除数
     * 3，	根据税率应用公式进行计算
     * 4，	计算行权扣税额: 扣税额= 计税收入* 税率-速算扣除数
     *
     * @param taxRateCalculation
     * @return
     */
    public static ApiResult withholdingServiceForSOS(SOSTaxRateInput taxRateCalculation) {
        boolean chinese = taxRateCalculation.isChinese();
        // 步骤: 1
        BigDecimal mped = taxRateCalculation.getMped();
        BigDecimal oep = taxRateCalculation.getOep();
        BigDecimal noS = taxRateCalculation.getNoS();
        BigDecimal taxableIncome;
        if (mped.doubleValue() < oep.doubleValue()) {
            taxableIncome = noS.multiply(oep);
        } else {
            taxableIncome = noS.multiply(mped.subtract(oep));
        }
        // 步骤: 2/3/4
        BigDecimal taxWithheld = calTaxWithheld(taxableIncome, chinese);
        return ApiResult.success(taxWithheld);
    }

    /**
     * SAS、RSU预扣税服务
     * 输入参数：
     * 1，	是否中国居民
     * 2，	原始每股归属价格(Vested Price) VEP
     * 3，	股权数量 NoS
     * 4，	归属日每股价格MPVD
     * 5，	免税比例，默认为0，CP
     * 6，	RSU 转变单元CU，默认为1
     * 需查询的参数： 税率Tax rate 和 速算扣除数 Quick deduction
     * 最后输出：Withholding tax 的值 (第4 8步计算结果)
     * <p>
     * 预扣税额计算规则：
     * 1，	应纳税收=NoS x (1 - CP) x VEP x CU
     * 2，	根据应纳税收额从表<二>中找到对应税率和速算扣除数
     * 3，	根据税率应用公式进行计算
     * 4，	计算行权扣税额，
     * 扣税额= 计税收入*税率-速算扣除数
     * Withholding tax = Taxable income x tax rate percent – quick deduction
     *
     * @param taxRateCalculation
     * @return
     */
    public static ApiResult withholdingServiceForSAS(SASTaxRateInput taxRateCalculation) {
        return withholdingServiceForRSU(taxRateCalculation);
    }

    public static ApiResult withholdingServiceForRSU(SASTaxRateInput taxRateCalculation) {
        boolean chinese = taxRateCalculation.isChinese();
        BigDecimal vep = taxRateCalculation.getVep();
        BigDecimal noS = taxRateCalculation.getNoS();
        BigDecimal mpvd = taxRateCalculation.getMpvd();
        BigDecimal cp = taxRateCalculation.getCp();
        BigDecimal cu = taxRateCalculation.getCu();
        // 步骤: 1
        BigDecimal taxableIncome = noS.multiply(new BigDecimal(1).subtract(cp)).multiply(vep).multiply(cu);
        // 步骤: 2/3/4
        BigDecimal taxWithheld = calTaxWithheld(taxableIncome, chinese);
        return ApiResult.success(taxWithheld);
    }
}
