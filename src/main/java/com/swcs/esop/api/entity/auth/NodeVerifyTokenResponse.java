package com.swcs.esop.api.entity.auth;

import com.swcs.esop.api.entity.auth.AuthData;
import lombok.Data;

/**
 * @author 阮程
 * @date 2022/10/24
 */
@Data
public class NodeVerifyTokenResponse {

    private String message;
    private AuthData authData;

}
