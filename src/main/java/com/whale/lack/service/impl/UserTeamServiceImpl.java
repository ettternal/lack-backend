package com.whale.lack.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whale.lack.model.domain.UserTeam;
import com.whale.lack.service.UserTeamService;
import com.whale.lack.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author serendipity
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-11-28 19:58:39
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




