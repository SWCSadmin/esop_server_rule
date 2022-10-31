package com.swcs.esop.api.entity;

import com.swcs.esop.api.enums.Status;

import java.text.MessageFormat;

/**
 * @author 阮程
 * @date 2022/10/21
 */
public class ApiResult<T> {
    /**
     * status
     */
    private Integer code;

    /**
     * message
     */
    private String msg;

    /**
     * data
     */
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

    public boolean isSuccess() {
        return this.isStatus(Status.SUCCESS);
    }

    public static <T> ApiResult<T> success() {
        return new ApiResult<>(Status.SUCCESS);
    }

    /**
     * Call this function if there is success
     *
     * @param data data
     * @param <T>  type
     * @return resule
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(data);
    }

    public boolean isStatus(Status status) {
        return this.code != null && this.code.equals(status.getCode());
    }

    public static ApiResult error(Status status) {
        return new ApiResult(status);
    }

    /**
     * Call this function if there is any error
     *
     * @param status status
     * @param args   args
     * @return ApiResult
     */
    public static ApiResult errorWithArgs(Status status, Object... args) {
        return new ApiResult(status.getCode(), MessageFormat.format(status.getMsg(), args));
    }

    public Integer getCode() {
        return code;
    }

    public ApiResult setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ApiResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public ApiResult setData(T data) {
        this.data = data;
        return this;
    }
}