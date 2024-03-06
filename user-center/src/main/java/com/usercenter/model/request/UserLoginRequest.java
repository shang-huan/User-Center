package com.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
/**
 * 用户登录请求包
 * @author ou
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3996996572149780233L;
    private String userAccount;
    private String userPassword;
}
