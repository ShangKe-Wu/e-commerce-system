package com.dhu.ecommercesystem.util;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author:WuShangke
 * @create:2022/7/14-20:41
 */
@Data
@ToString
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private int resultCode;
    private String message;
    private T data;

    public Result() {
    }

    public Result(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }
}
