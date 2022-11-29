package com.swcs.esop.api.module.excel;

import com.swcs.esop.api.entity.ParticipantInfo;

import java.util.List;

/**
 * @author 阮程
 * @date 2022/11/1
 */
public class ParticipantInfoReadListener extends BaseReadListener<ParticipantInfo> {

    public ParticipantInfoReadListener(boolean upsert) {
        super(upsert);
    }

    @Override
    protected void dataValid(ParticipantInfo o, List<String> errorList) {
        super.dataValid(o, errorList);
    }

    @Override
    protected void beforeInvoke(ParticipantInfo o) {
        super.beforeInvoke(o);
    }
}
