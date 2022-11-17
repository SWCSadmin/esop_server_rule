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
public class SASTaxRateInput implements BaseTaxRuleInput {

    /**
     * 是否中国公民
     */
    private Boolean chinese;
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

    @Override
    public ApiResult paramsValid() {
        if (chinese == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "chinese");
        }
        if (vep == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "vep");
        }
        if (noS == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "noS");
        }
        return ApiResult.success();
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
     * @return
     */
    @Override
    public ApiResult ruleCalculation() {
        // 步骤: 1
        BigDecimal taxableIncome = noS.multiply(new BigDecimal(1).subtract(cp)).multiply(vep).multiply(cu);
        // 步骤: 2/3/4
        BigDecimal taxWithheld = calTaxWithheld(taxableIncome, chinese);
        return ApiResult.success(taxWithheld);
    }
}
