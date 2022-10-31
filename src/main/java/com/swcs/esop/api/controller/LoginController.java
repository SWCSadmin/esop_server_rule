package com.swcs.esop.api.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.swcs.esop.api.common.Constants;
import com.swcs.esop.api.config.AppProperties;
import com.swcs.esop.api.entity.ApiResult;
import com.swcs.esop.api.entity.NodeVerifyTokenResponse;
import com.swcs.esop.api.entity.User;
import com.swcs.esop.api.util.NodeServiceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 阮程
 * @date 2022/10/24
 */
@RestController
public class LoginController extends BaseController {

    public static final ConcurrentHashMap<String, NodeVerifyTokenResponse> TOKEN_MAP = new ConcurrentHashMap<>();

    @Autowired
    protected AppProperties appProperties;

    @PostMapping("/login")
    public ApiResult login(@RequestBody User user) {
        ApiResult apiResult = NodeServiceUtil.getLoginToken(user.getLogin_id());
        if (apiResult.isSuccess()) {
            apiResult = NodeServiceUtil.verifyToken(user);
            if (apiResult.isSuccess()) {
                NodeVerifyTokenResponse response = (NodeVerifyTokenResponse) apiResult.getData();
                TOKEN_MAP.put(user.getLogin_id(), response);
            }
        }
        return apiResult;
    }

    @PostMapping("/logout")
    public ApiResult logout() {
        String auth = request.getHeader("authorization");
        if (StringUtils.isNotEmpty(auth)) {
            String[] auths = auth.split(" ");
            if (auths.length == 2) {
                try {
                    String user = JWT.decode(auths[1]).getClaims().get("user").asString();
                    request.removeAttribute(Constants.SESSION_USER);
                    TOKEN_MAP.remove(user);
                } catch (JWTDecodeException e) {
                }
            }
        }
        return ApiResult.success();
    }
}
