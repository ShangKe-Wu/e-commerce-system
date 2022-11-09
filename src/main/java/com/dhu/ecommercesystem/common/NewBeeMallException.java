package com.dhu.ecommercesystem.common;

/**
 * @author:WuShangke
 * @create:2022/7/14-20:58
 * 自定义异常
 */
public class NewBeeMallException extends RuntimeException{
    public NewBeeMallException() {
    }

    public NewBeeMallException(String message) {
        super(message);
    }
    /**
     * 丢出一个异常
     * @param message
     */
    public static void fail(String message) {
        throw new NewBeeMallException(message);
    }
}
