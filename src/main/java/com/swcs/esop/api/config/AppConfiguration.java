/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swcs.esop.api.config;

import com.alibaba.fastjson2.JSON;
import com.swcs.esop.api.common.Constants;
import com.swcs.esop.api.entity.ApiResult;
import com.swcs.esop.api.enums.Status;
import com.swcs.esop.api.interceptor.interceptor.LocaleChangeInterceptor;
import com.swcs.esop.api.interceptor.interceptor.LoginHandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

/**
 * application configuration
 */
@Configuration
public class AppConfiguration implements WebMvcConfigurer {
    public static final Logger logger = LoggerFactory.getLogger(AppConfiguration.class);

    public static final String PATH_PATTERN = "/**";
    public static final String LOGIN_INTERCEPTOR_PATH_PATTERN = "/api/*";

    /**
     * 跨域配置
     *
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration(PATH_PATTERN, config);
        return new CorsFilter(configSource);
    }

    /**
     * Cookie
     *
     * @return local resolver
     */
    @Bean(name = "localeResolver")
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieName(Constants.LOCALE_LANGUAGE);
        // set default locale
        localeResolver.setDefaultLocale(Locale.US);
        // set language tag compliant
        localeResolver.setLanguageTagCompliant(false);
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        return new LocaleChangeInterceptor();
    }

    @Bean
    public LoginHandlerInterceptor loginInterceptor() {
        return new LoginHandlerInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // i18n
        registry.addInterceptor(localeChangeInterceptor());
        // 权限校验
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns(LOGIN_INTERCEPTOR_PATH_PATTERN)
                .excludePathPatterns(
                        "/login"
                        , "/swagger-resources/**", "/swagger-ui.html", "/webjars/**"    // swagger
                );
    }


    /**
     * 统一异常处理
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(myHandlerExceptionResolver());
    }

    @Bean
    public HandlerExceptionResolver myHandlerExceptionResolver() {
        return (request, response, handler, e) -> {
            logger.error(e.getMessage(), e);
            responseResult(response, ApiResult.errorWithArgs(Status.INTERNAL_SERVER_ERROR_ARGS, e.getMessage()));
            return new ModelAndView();
        };
    }

    private static void responseResult(HttpServletResponse response, ApiResult result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        PrintWriter writer;
        try {
            writer = response.getWriter();
            writer.write(JSON.toJSONString(result));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error("write response error", e);
        }
    }
}
