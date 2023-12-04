package com.whale.lack.service;

import com.whale.lack.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.whale.lack.model.domain.User;
import com.whale.lack.model.request.TeamJoinRequest;
import com.whale.lack.model.request.TeamQuitRequest;
import com.whale.lack.model.request.TeamUpdateRequest;
import com.whale.lack.model.vo.TeamUserVO;
import com.whale.lack.service.dto.TeamQuery;

import java.util.List;

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

    /**
     * 搜索队伍
     * @param teamQuery 查询队伍
     * @param isAdmin 是否是管理员
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 队长删除（解散）队伍
     * @param id
     * @return
     */
    boolean deleteTeam(long id,User loginUser);
}
