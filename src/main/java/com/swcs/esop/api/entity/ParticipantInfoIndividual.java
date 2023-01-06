package com.swcs.esop.api.entity;

import com.swcs.esop.api.module.excel.ExcelCheckEnum;
import com.swcs.esop.api.module.excel.annotion.ExcelCheckField;
import lombok.Data;

/**
 * @author 阮程
 * @date 2022/12/13
 */
@Data
public class ParticipantInfoIndividual extends ParticipantInfo {

    @ExcelCheckField(value = {ExcelCheckEnum.NotEmpty})
    private String user_input_id_z;
    private String participant_id;

    public LoginAccount getLoginAccount() {
        LoginAccount loginAccount = new LoginAccount();
        loginAccount.setLogin_id(getParticipant_id());
        loginAccount.setLogin_pwd(getParticipant_id());
        return loginAccount;
    }

}
