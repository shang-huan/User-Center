package com.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
import java.io.StringReader;

@Data
public class ModifyUserRequest implements Serializable {

    private static final long serialVersionUID = 7752803555564125155L;
    private String userAccount;
    private String username;
    private String phone;
    private String email;
    private String avatarUrl;
    private Integer gender;
}
