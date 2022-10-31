package com.swcs.esop.api.interceptor.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.swcs.esop.api.common.Constants;
import com.swcs.esop.api.config.AppProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.swcs.esop.api.controller.LoginController.TOKEN_MAP;

/**
 * @author 阮程
 * @date 2022/10/24
 */
public class LoginHandlerInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoginHandlerInterceptor.class);

    @Autowired
    private AppProperties appProperties;

    /**
     * Intercept the execution of a handler. Called after HandlerMapping determined
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     * @return boolean true or false
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!appProperties.isAuth()) {
            return true;
        }

        String auth = request.getHeader("authorization");
        if (StringUtils.isEmpty(auth)) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            logger.info("user does not exist");
            return false;
        } else {
            String[] auths = auth.split(" ");
            if (auths.length == 2) {
                try {
                    DecodedJWT decodedJWT = JWT.decode(auths[1]);

                    String user = decodedJWT.getClaims().get("user").asString();
                    long iat = JWT.decode(auths[1]).getClaims().get("iat").asLong();    // 发布时间
                    long exp = JWT.decode(auths[1]).getClaims().get("exp").asLong();    // 过期时间
                    request.setAttribute(Constants.SESSION_USER, user);
                    if ("Bearer".equals(auths[0])) {
                        if (TOKEN_MAP.containsKey(user)) {
                            return true;
                        } else {
                            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                            logger.info("user not login");
                            return false;
                        }
                    }
                } catch (JWTDecodeException e) {
                    logger.warn("token 解析异常", e);
                }
            }
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            logger.info("token is not valid");
            return false;
        }
    }
}
