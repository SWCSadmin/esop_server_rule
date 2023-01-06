package com.swcs.esop.api.module.excel;

import com.swcs.esop.api.entity.TrustTransactions;

import java.util.List;

/**
 * 批量新增
 *
 * @author 阮程
 * @date 2022/11/1
 */
public class TrustTransactionsReadListener extends BaseReadListener<TrustTransactions> {

    private String startDate;
    private String endDate;

    public TrustTransactionsReadListener(boolean upsert, String startDate, String endDate) {
        super(upsert, "transactions");
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    protected void beforeInvoke(TrustTransactions o, List<String> errorList) {
        super.beforeInvoke(o, errorList);
        o.setStartDate(this.startDate);
        o.setEndDate(this.endDate);
        o.setTransaction_id(o.getPrimaryKey());
    }

    @Override
    protected void dataValid(TrustTransactions o, List<String> errorList) {
    }

}
