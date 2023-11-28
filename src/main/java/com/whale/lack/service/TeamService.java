package com.whale.lack.service;

import com.whale.lack.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.whale.lack.model.domain.User;

/**
* @author serendipity
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-11-28 19:33:44
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     * @param team
     * @param longinUser
     * @return
     */
    long addTeam(Team team, User longinUser);
}
