package com.swcs.esop.api.entity.auth;

import lombok.Data;

/**
 * @author 阮程
 * @date 2022/10/24
 */
@Data
public class AuthData {

    private String user;
    private long iat;
    private long exp;

}
