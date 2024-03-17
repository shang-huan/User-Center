package com.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = -2178300884716240685L;

    /**
     * 错误码
     */
    private int code;
    /**
     * 数据
     */
    private T data;
    /**
     * 消息
     */
    private String message;

    /**
     *
     */
    private String description;

    public BaseResponse() {
    }

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null, errorCode.getMessage(), errorCode.getDescription());
    }
}
