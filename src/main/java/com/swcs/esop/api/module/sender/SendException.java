package com.swcs.esop.api.module.sender;

/**
 * @author 阮程
 * @date 2018-12-10
 */
public class SendException extends RuntimeException {
    public SendException() {
        super();
    }

    public SendException(String message) {
        super(message);
    }
}
