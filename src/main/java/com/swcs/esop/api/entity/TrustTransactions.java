package com.swcs.esop.api.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.swcs.esop.api.common.base.ExcelUploadEntity;
import com.swcs.esop.api.module.excel.ExcelCheckEnum;
import com.swcs.esop.api.module.excel.annotion.ExcelCheckField;
import com.swcs.esop.api.module.excel.annotion.ExcelDateFormat;
import com.swcs.esop.api.module.excel.annotion.ExcelNumberFormat;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 阮程
 * @date 2022/11/16
 */
@Data
public class TrustTransactions extends ExcelUploadEntity {

    @ExcelIgnore
    private String transaction_id;

    @ExcelCheckField(ExcelCheckEnum.NotEmpty)
    private String trust_account_id;
    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    @ExcelDateFormat(value = "yyyy-MM-dd", original = "yyyy/MM/dd")
    private String date;
    @ExcelCheckField(ExcelCheckEnum.NotEmpty)
    @ExcelNumberFormat(value = "%02d")
    private String day_transaction_order;
    @ExcelCheckField(ExcelCheckEnum.NotEmpty)
    private String transaction_type;
    @ExcelCheckField(ExcelCheckEnum.Double)
    private String cash_amount;
    @ExcelCheckField(ExcelCheckEnum.Double)
    private String share_amount;
    @ExcelCheckField(ExcelCheckEnum.Double)
    private String market_share_price;
    private String remarks_en;
    private String remarks_sc;
    private String remarks_tc;

    @ExcelIgnore
    private String startDate;
    @ExcelIgnore
    private String endDate;

    @Override
    public String getPrimaryKey() {
        return String.format("%s.%s.%s", trust_account_id, date, day_transaction_order);
    }

    @Override
    public boolean primaryKeyIsBlank() {
        return StringUtils.isBlank(trust_account_id) || StringUtils.isBlank(date) || StringUtils.isBlank(day_transaction_order);
    }
}
