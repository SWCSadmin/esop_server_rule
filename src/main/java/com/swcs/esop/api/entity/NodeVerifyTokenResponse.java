package com.swcs.esop.api.entity;

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
