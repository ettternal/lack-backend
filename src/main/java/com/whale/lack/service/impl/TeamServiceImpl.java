package com.whale.lack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whale.lack.common.ErrorCode;
import com.whale.lack.enums.TeamStatusEnum;
import com.whale.lack.exception.BusinessException;
import com.whale.lack.model.domain.Team;
import com.whale.lack.model.domain.User;
import com.whale.lack.model.domain.UserTeam;
import com.whale.lack.model.request.TeamJoinRequest;
import com.whale.lack.model.request.TeamQuitRequest;
import com.whale.lack.model.request.TeamUpdateRequest;
import com.whale.lack.model.vo.TeamUserVO;
import com.whale.lack.model.vo.UserVO;
import com.whale.lack.service.TeamService;
import com.whale.lack.mapper.TeamMapper;
import com.whale.lack.service.UserService;
import com.whale.lack.service.UserTeamService;
import com.whale.lack.service.dto.TeamQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author serendipity
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2023-11-28 19:33:44
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;

    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //1.请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        final long userId = loginUser.getId();
        //3.检验信息
        //(1).队伍人数>1且<=20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);//如果为空，直接赋值为0
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        //(2).队伍标题 <=20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        // (3) 描述<= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        //(4)status 是否公开，不传默认为0
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        //(5)如果status是加密状态，一定要密码 且密码<=32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
        }
        //(6)超出时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超出时间 > 当前时间");
        }
        //(7)校验用户最多创建5个队伍
        //todo 有bug。可能同时创建100个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }
        //4.插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (!result || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        //5. 插入用户 ==> 队伍关系 到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        return teamId;
    }

//    /**
//     * 搜索队伍
//     * @param teamQuery
//     * @return
//     */
//    @Override
//    public List<TeamUserVo> listTeams(TeamQuery teamQuery,boolean isAdmin) {
//        //1. 组合查询条件
//        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
//        if(teamQuery != null){
//            Long id = teamQuery.getId();
//            if(id != null && id > 0){
//                queryWrapper.eq("id",id);
//            }
//            String searchText = teamQuery.getSearchText();
//            if(StringUtils.isNotBlank(searchText)){
//                queryWrapper.and(qw -> qw.like("name",searchText).or().like("description",searchText));
//            }
//            String name = teamQuery.getName();
//            if(StringUtils.isNotBlank(name)){
//                queryWrapper.like("name",name);
//            }
//            String description = teamQuery.getDescription();
//            if(StringUtils.isNotBlank(description)){
//                queryWrapper.like("description",description);
//            }
//            Integer maxNum = teamQuery.getMaxNum();
//            if(maxNum != null && maxNum > 0){
//                queryWrapper.eq("maxNum",maxNum);
//            }
//            Long userId = teamQuery.getUserId();
//            //创建人查询
//            if(userId != null && userId > 0){
//                queryWrapper.eq("userId",userId);
//            }
//            //根据状态查询
//            Integer status = teamQuery.getStatus();
//            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
//            if(statusEnum == null){
//                statusEnum = TeamStatusEnum.PUBLIC;
//            }
//            if(!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)){
//                throw new BusinessException(ErrorCode.NO_AUTH);
//            }
//            queryWrapper.eq("status",statusEnum.getValue());
//        }
//        //不展示过期的队伍
//        //expireTime is null or expireTime > now()
//        queryWrapper.and(qw -> qw.gt("expireTime",new Date()).or().isNull("expireTime"));
//        List<Team> teamList = this.list(queryWrapper);
//        if(CollectionUtils.isEmpty(teamList)){
//            return new ArrayList<>();
//        }
//        //关联查询用户信息
//        //1.自己写sql
//        //查询队伍和创建人的信息：select * from team t left join user u on t.userId = u.id
//        //查询队伍和已经加入队伍的信息：
//        // select *
//        //from team t
//        //         left join user_team ut on t.id = ut.teamId
//        //         left join user u on ut.userId = u.id;
//        List<TeamUserVo> teamUserVoList = new ArrayList<>();
//        //关联查询创建人的用户信息
//        //遍历队伍表拿到创建人id，在关联查询创建人的信息
//        for (Team team : teamList){
//           Long  userId = team.getUserId();
//           if(userId == null){
//               continue;
//           }
//            User user = userService.getById(userId);
//            TeamUserVo teamUserVo = new TeamUserVo();
//            BeanUtils.copyProperties(team,teamUserVo);
//
//           //用户信息脱敏
//            UserVo userVo = new UserVo();
//            if(user != null){
//                BeanUtils.copyProperties(user,userVo);
//                teamUserVo.setCreateUser(userVo);
//            }
//            teamUserVoList.add(teamUserVo);
//        }
//        return teamUserVoList;
//    }
    /**
     * 搜索队伍
     * @param teamQuery
     * @return
     */
@Override
public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
    QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
    // 组合查询条件
    if (teamQuery != null) {
        Long id = teamQuery.getId();
        if (id != null && id > 0) {
            queryWrapper.eq("id", id);
        }
        String searchText = teamQuery.getSearchText();
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
        }
        String name = teamQuery.getName();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        String description = teamQuery.getDescription();
        if (StringUtils.isNotBlank(description)) {
            queryWrapper.like("description", description);
        }
        Integer maxNum = teamQuery.getMaxNum();
        // 查询最大人数相等的
        if (maxNum != null && maxNum > 0) {
            queryWrapper.eq("maxNum", maxNum);
        }
        Long userId = teamQuery.getUserId();
        // 根据创建人来查询
        if (userId != null && userId > 0) {
            queryWrapper.eq("userId", userId);
        }
        // 根据状态来查询
        Integer status = teamQuery.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            statusEnum = TeamStatusEnum.PUBLIC;
        }
        if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        queryWrapper.eq("status", statusEnum.getValue());
    }
    // 不展示已过期的队伍
    // expireTime is null or expireTime > now()
    queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
    List<Team> teamList = this.list(queryWrapper);
    if (CollectionUtils.isEmpty(teamList)) {
        return new ArrayList<>();
    }
    List<TeamUserVO> teamUserVOList = new ArrayList<>();
    // 关联查询创建人的用户信息
    for (Team team : teamList) {
        Long userId = team.getUserId();
        if (userId == null) {
            continue;
        }
        User user = userService.getById(userId);
        TeamUserVO teamUserVO = new TeamUserVO();
        BeanUtils.copyProperties(team, teamUserVO);
        // 脱敏用户信息
        if (user != null) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            teamUserVO.setCreateUser(userVO);
        }
        teamUserVOList.add(teamUserVO);
    }
    return teamUserVOList;
}


    /**
     * 更新队伍
     * @param teamUpdateRequest 更新队伍需要传入的参数对象
     * @return
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser) {
        if(teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if(id == null || id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if(oldTeam == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");

        }
        //只有管理员或者队伍的创建者可以修改
        if(oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if(statusEnum.equals(TeamStatusEnum.SECRET)){
            if(StringUtils.isNotBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间必须要设置参数！");
            }

        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,updateTeam);
        return this.updateById(updateTeam);
    }

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if(teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = loginUser.getId();

        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if(expireTime != null && team.getExpireTime().before(new Date())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        //禁止加入私有队伍
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if(TeamStatusEnum.PRIVATE.equals(teamStatusEnum)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入私有队伍");

        }
        String password = teamJoinRequest.getPassword();
        if(TeamStatusEnum.SECRET.equals(teamStatusEnum)){
            if(StringUtils.isBlank(password) || !password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
            }
        }
        //关联查询，用户已经加入的队伍数
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",userId);
        long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if(hasJoinNum > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多创建和加入5个队伍！");
        }
        //不能重复加入已经加入的队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",userId);
        userTeamQueryWrapper.eq("teamId",teamId);
        long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
        if(hasUserJoinTeam > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户已经加入队伍！");
        }
        //查询已经加入队伍的人数
        long teamHasJoinNum = countTeamUserByTeamId(userId);
        if(teamHasJoinNum >= team.getMaxNum()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
        }
        //修改队伍信息
        UserTeam userTeam = new UserTeam();
        userTeam.setJoinTime(new Date());
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        return userTeamService.save(userTeam);

    }

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    @Override
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long teamId = loginUser.getId();
        Team team = getTeamById(teamId);
        long userId = loginUser.getId();
        //判断是否已经加入队伍
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }
        //查看当前队伍的人数
        //从队伍id中查看该队伍中有多少人
        long countTeamUserByTeamId = countTeamUserByTeamId(teamId);
        if (countTeamUserByTeamId == 1) {
            //删除id
            this.removeById(teamId);
        } else {
            //队伍至少还有两人
            //是否是队长
            //是队长
            if (team.getUserId() == userId) {
                //把队伍转移给最早加入的用户
                //1.查询已加入的所有用户的时间
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR);
                }
                    UserTeam nextUserTeam = userTeamList.get(1);
                    Long nextTeamLeaderId = nextUserTeam.getId();
                    //更新为当前队长
                    Team updateTeam = new Team();
                    updateTeam.setId(teamId);
                    updateTeam.setUserId(nextTeamLeaderId);
                    boolean result = this.updateById(updateTeam);
                    if (!result) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新队长失败！");
                    }
                    //移除加入队伍的队伍关系
                }
                //不是队长
        }
        return userTeamService.remove(queryWrapper);
    }


    /**
     * 队长解散队伍
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id,User loginUser) {
        //1.校验请求参数
        Team team = getTeamById(id);
        //2.校验是不是队伍的队长
        if(team.getUserId() != loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH,"无访问权限！");
        }
        //3.移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        long id1 = team.getId();
        userTeamQueryWrapper.eq("teamId", id1);
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if(!result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"删除队伍关联信息失败");
        }
        //4.删除队伍
        return this.removeById(id1);
    }

    /**
     * 根据id查询队伍信息
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入队伍id");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在！");
        }
        return team;
    }

    /**
     * 根据队伍id查询改队伍有多少人
     * @param teamId 队伍id
     * @return
     */
    private long countTeamUserByTeamId(long teamId){
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId",teamId);
        long teamHasJoinNum = userTeamService.count(userTeamQueryWrapper);
        return teamHasJoinNum;
    }
}




