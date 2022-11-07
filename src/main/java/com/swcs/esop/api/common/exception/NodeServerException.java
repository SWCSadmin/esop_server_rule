package com.swcs.esop.api.common.exception;

/**
 * @author 阮程
 * @date 2022/11/1
 */
public class NodeServerException extends RuntimeException {

    public NodeServerException() {
        super();
    }

    public NodeServerException(String message) {
        super(message);
    }

    public NodeServerException(Throwable cause) {
        super(cause);
    }
}
