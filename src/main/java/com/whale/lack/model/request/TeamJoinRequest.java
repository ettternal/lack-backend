package com.whale.lack.model.request;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class TeamJoinRequest implements Serializable {
    private static final long serialVersionUID = 941141758449488001L;
    /**
     * 队伍名称
     */
    private Long teamId;


    /**
     * 密码
     */
    private String password;

}
