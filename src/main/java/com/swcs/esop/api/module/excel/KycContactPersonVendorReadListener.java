package com.swcs.esop.api.module.excel;

import com.swcs.esop.api.entity.KycContactPersonVendor;

import java.util.List;

/**
 * @author 阮程
 * @date 2023/1/6
 */
public class KycContactPersonVendorReadListener extends BaseReadListenerReturnStatus<KycContactPersonVendor> {

    public KycContactPersonVendorReadListener() {
        super(true, "contact_person");
    }

    @Override
    protected void dataValid(KycContactPersonVendor o, List<String> errorList) {
        if (!o.primaryKeyIsBlank()) {
            List<KycContactPersonVendor> list = listDbRecord(o);
            if (list.isEmpty()) {
                errorList.add("contact_id " + getMessage("IS_INVALID_VALUE"));
            }
        }

    }
}
