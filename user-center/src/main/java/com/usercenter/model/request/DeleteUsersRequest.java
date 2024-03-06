package com.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteUsersRequest implements Serializable {
    private static final long serialVersionUID = -3689602694175005931L;

    private long id;
}
