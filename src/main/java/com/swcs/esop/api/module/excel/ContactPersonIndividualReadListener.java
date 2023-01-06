package com.swcs.esop.api.module.excel;

import com.swcs.esop.api.entity.ContactPersonIndividual;
import com.swcs.esop.api.entity.ParticipantInfoIndividual;

import java.util.List;
import java.util.Map;

/**
 * @author 阮程
 * @date 2022/12/9
 */
public class ContactPersonIndividualReadListener extends BaseReadListenerReturnStatus<ContactPersonIndividual> {

    private ParticipantInfoIndividualReadListener participantInfoReadListener;

    public ContactPersonIndividualReadListener(boolean upsert, ParticipantInfoIndividualReadListener participantInfoReadListener) {
        super(upsert, "contact_person");
        this.participantInfoReadListener = participantInfoReadListener;
    }

    @Override
    protected void beforeInvoke(ContactPersonIndividual o, List<String> errorList) {
        // 关联 participantInfo 记录
        Map<String, ParticipantInfoIndividual> userInputIdMap = participantInfoReadListener.getUserInputIdMap();
        if (userInputIdMap != null) {
            o.setEntity_id(userInputIdMap.get(o.getUser_input_id_z()).getParticipant_id());
        }
        super.beforeInvoke(o, errorList);
    }

    @Override
    protected void dataValid(ContactPersonIndividual o, List<String> errorList) {
        if (!o.primaryKeyIsBlank()) {
            List<ContactPersonIndividual> list = listDbRecord(o);
            if (list.isEmpty()) {
                errorList.add("concat_id " + getMessage("IS_INVALID_VALUE"));
            }
        }
    }

}
