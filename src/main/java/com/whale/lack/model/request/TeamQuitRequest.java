package com.whale.lack.model.request;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class TeamQuitRequest implements Serializable {
    private static final long serialVersionUID = 941141758449488001L;
    /**
     * 队伍id
     */
    private Long TeamId;

}
