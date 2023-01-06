package com.swcs.esop.api.module.excel;

import com.swcs.esop.api.entity.KycVendorInfo;

import java.util.List;

/**
 * @author 阮程
 * @date 2022/12/9
 */
public class KycVendorInfoReadListener extends BaseReadListenerReturnStatus<KycVendorInfo> {

    public KycVendorInfoReadListener() {
        super(true, "vendor_info");
    }

    @Override
    protected void dataValid(KycVendorInfo o, List<String> errorList) {
        if (!o.primaryKeyIsBlank()) {
            List<KycVendorInfo> list = listDbRecord(o);
            if (list.isEmpty()) {
                errorList.add("vendor_id " + getMessage("IS_INVALID_VALUE"));
            }
        }
    }

}
