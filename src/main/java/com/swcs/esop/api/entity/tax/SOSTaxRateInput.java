package com.swcs.esop.api.entity.tax;

import com.swcs.esop.api.common.base.BaseTaxRuleInput;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.enums.Status;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 阮程
 * @date 2022/10/31
 */
@Data
public class SOSTaxRateInput implements BaseTaxRuleInput {

    /**
     * 是否中国公民
     */
    private Boolean chinese;
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

    @Override
    public ApiResult paramsValid() {
        if (chinese == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "chinese");
        }
        if (oep == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "oep");
        }
        if (noS == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "noS");
        }
        if (mped == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "mped");
        }
        return ApiResult.success();
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
     * @return
     */
    @Override
    public ApiResult ruleCalculation() {
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

}
