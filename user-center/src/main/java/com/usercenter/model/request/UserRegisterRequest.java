package com.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求包
 * @author ou
 */
@Data
public class UserRegisterRequest implements Serializable{
    private static final long serialVersionUID = 6760803017313926976L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
