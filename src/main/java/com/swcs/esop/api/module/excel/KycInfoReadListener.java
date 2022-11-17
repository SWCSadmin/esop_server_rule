package com.swcs.esop.api.module.excel;

import com.swcs.esop.api.entity.KycInfo;

/**
 * @author 阮程
 * @date 2022/11/1
 */
public class KycInfoReadListener extends BaseReadListener<KycInfo> {

    public KycInfoReadListener(boolean upsert) {
        super(upsert);
    }

}
