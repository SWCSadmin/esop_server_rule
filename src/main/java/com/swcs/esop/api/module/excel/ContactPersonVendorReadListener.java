package com.swcs.esop.api.module.excel;

import com.swcs.esop.api.entity.ContactPersonVendor;
import com.swcs.esop.api.entity.ParticipantInfoVendor;

import java.util.List;
import java.util.Map;

/**
 * @author 阮程
 * @date 2022/12/9
 */
public class ContactPersonVendorReadListener extends BaseReadListenerReturnStatus<ContactPersonVendor> {

    private ParticipantInfoVendorReadListener participantInfoReadListener;

    public ContactPersonVendorReadListener(boolean upsert, ParticipantInfoVendorReadListener participantInfoReadListener) {
        super(upsert, "contact_person");
        this.participantInfoReadListener = participantInfoReadListener;
    }

    @Override
    protected void beforeInvoke(ContactPersonVendor o, List<String> errorList) {
        // 关联 participantInfo 记录
        Map<String, ParticipantInfoVendor> userInputIdMap = participantInfoReadListener.getUserInputIdMap();
        if (userInputIdMap != null) {
            o.setEntity_id(userInputIdMap.get(o.getUser_input_id_x()).getParticipant_id());
        }
        super.beforeInvoke(o, errorList);
    }

    @Override
    protected void dataValid(ContactPersonVendor o, List<String> errorList) {
        if (!o.primaryKeyIsBlank()) {
            List<ContactPersonVendor> list = listDbRecord(o);
            if (list.isEmpty()) {
                errorList.add("concat_id " + getMessage("IS_INVALID_VALUE"));
            }
        }
    }

}
