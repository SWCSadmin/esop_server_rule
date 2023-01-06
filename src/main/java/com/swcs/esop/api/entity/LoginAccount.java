package com.swcs.esop.api.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author 阮程
 * @date 2022/12/14
 */
@Data
public class LoginAccount {

    private String login_id;
    private String login_pwd;
    private String account_status = "1";
    private String failed_attempts = "0";
    private Date last_login_date;
}
