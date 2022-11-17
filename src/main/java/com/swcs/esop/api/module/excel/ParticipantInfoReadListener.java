package com.swcs.esop.api.module.excel;

import com.swcs.esop.api.entity.ParticipantInfo;

/**
 * @author 阮程
 * @date 2022/11/1
 */
public class ParticipantInfoReadListener extends BaseReadListener<ParticipantInfo> {

    public ParticipantInfoReadListener(boolean upsert) {
        super(upsert);
    }

}
