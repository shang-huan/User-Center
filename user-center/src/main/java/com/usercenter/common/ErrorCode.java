package com.usercenter.common;

/**
 * 错误码
 */
public enum ErrorCode {
    /**
     * SUCCESS:成功
     * PARAMS_ERROR:请求参数错误
     * NULL_ERROR:请求数据为空
     * NO_AUTH_ERROR:无权限
     * NO_LOGIN_ERROR:未登录
     */
    SUCCESS(0,"ok",""),
    PARAMS_ERROR(40000,"请求参数错误",""),
    NULL_ERROR(40001,"请求数据为空",""),
    NO_AUTH_ERROR(40100,"无权限",""),
    NO_LOGIN_ERROR(40101,"未登录",""),
    SYSTEM_ERROR(50000,"系统内部异常","");
    /**
     * 状态码
     */
    private final int code;
    /**
     * 状态码信息
     */
    private final String message;
    /**
     * 状态码描述
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
