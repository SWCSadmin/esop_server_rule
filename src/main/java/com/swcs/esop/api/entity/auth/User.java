package com.swcs.esop.api.entity.auth;

import lombok.Data;

/**
 * @author 阮程
 * @date 2022/10/24
 */
@Data
public class User {

    private String login_id;
    private String login_pwd;
    private UserToken token;

}
