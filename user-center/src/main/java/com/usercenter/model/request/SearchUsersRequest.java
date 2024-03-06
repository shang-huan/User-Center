package com.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchUsersRequest implements Serializable {
    private static final long serialVersionUID = 5015890658311806327L;
    private String userAccount;
}
