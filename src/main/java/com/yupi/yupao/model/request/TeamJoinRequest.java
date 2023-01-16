package com.yupi.yupao.model.request;

import lombok.Data;

import java.util.Date;

@Data
public class TeamJoinRequest {

    private Long teamId;
    /**
     * 密码
     */
    private String password;


}
