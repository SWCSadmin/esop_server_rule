package com.swcs.esop.api.common.mvc;

import com.alibaba.fastjson2.JSON;
import com.swcs.esop.api.enums.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;

/**
 * @author 阮程
 * @date 2022/10/21
 */
public class ApiResult<T> {

    public static final Logger logger = LoggerFactory.getLogger(ApiResult.class);

    private boolean success;

    private Integer code;

    private String msg;

    private T data;


    private ApiResult() {
    }

    private ApiResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private ApiResult(T data) {
        this.code = 0;
        this.data = data;
    }

    private ApiResult(Status status) {
        if (status != null) {
            this.code = status.getCode();
            this.msg = status.getMsg();
        }
    }

    public static <T> ApiResult<T> success() {
        return new ApiResult<T>(Status.SUCCESS).setSuccess(true);
    }

    public static <T> ApiResult<T> success(Status status) {
        return new ApiResult<T>(status).setSuccess(true);
    }

    public static <T> ApiResult<T> successWithArgs(Status status, Object... args) {
        return new ApiResult<T>(status.getCode(), MessageFormat.format(status.getMsg(), args)).setSuccess(true);
    }

    /**
     * Call this function if there is success
     *
     * @param data data
     * @param <T>  type
     * @return resule
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(data).setSuccess(true);
    }

    public static ApiResult error(Status status) {
        return new ApiResult(status).setSuccess(false);
    }

    /**
     * Call this function if there is any error
     *
     * @param status status
     * @param args   args
     * @return ApiResult
     */
    public static ApiResult errorWithArgs(Status status, Object... args) {
        return new ApiResult(status.getCode(), MessageFormat.format(status.getMsg(), args)).setSuccess(false);
    }


    public boolean isSuccess() {
        return success;
    }

    public ApiResult<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public ApiResult<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ApiResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public ApiResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public static void responseResult(HttpServletResponse response, ApiResult result) {
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