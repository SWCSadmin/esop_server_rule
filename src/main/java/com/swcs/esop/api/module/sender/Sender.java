package com.swcs.esop.api.module.sender;

/**
 * @author 阮程
 * @date 2018-12-10
 */
public interface Sender {

    boolean send() throws SendException;

}
