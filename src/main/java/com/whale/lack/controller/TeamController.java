package com.whale.lack.controller;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whale.lack.model.domain.Team;
import com.whale.lack.model.domain.User;
import com.whale.lack.model.request.TeamAddRequest;
import com.whale.lack.service.TeamService;
import com.whale.lack.service.UserService;
import com.whale.lack.common.BaseResponse;
import com.whale.lack.common.ErrorCode;
import com.whale.lack.common.ResultUtils;
import com.whale.lack.exception.BusinessException;
import com.whale.lack.service.dto.TeamQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


/**
 * 队伍接口
 */
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
@Slf4j //lombok的注解，可以在类中使用log打日志
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;
    private TeamQuery teamQuery;

    /**
     * 增加队伍
     * @param teamAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest,
                                      HttpServletRequest request){
        if (teamAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    /**
     * 删除队伍
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id){//接收前端传来队伍的信息
        if(id <= 0){
            throw new  BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.removeById(id);//teamService继承自Iservices的接口,底层实现了serviceImpl
        //需要返回新生成数据的id,使用mybatis的组件回写
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);


    }
    /**
     * 改动队伍
     *
     * @param team
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody Team team){//接收前端传来队伍的信息
        if(team == null){
            throw new  BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.updateById(team);//teamService继承自Iservices的接口,底层实现了serviceImpl
        //需要返回新生成数据的id,使用mybatis的组件回写
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(true);


    }

    /**
     * 查询队伍
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(@RequestBody long id){//接收前端传来队伍id的信息
        if(id <= 0){
            throw new  BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);//teamService继承自Iservices的接口,底层实现了serviceImpl
        //需要返回新生成数据的id,使用mybatis的组件回写
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"数据为空！");
        }
        return ResultUtils.success(team);


    }

    /**
     * 查询组队列表
     * @param teamQuery
     * @return
     */
    @GetMapping("/list")
    //新建teamQuery业务请求参数封装类作为，原因：1.请求参数和实体类不一样；2.有些参数用不到；3.有些字段要隐藏不返回到前端
    public BaseResponse<List<Team>> listTeams(TeamQuery teamQuery){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(team,teamQuery);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        List<Team> teamList = teamService.list(queryWrapper);
        return ResultUtils.success(teamList);
    }

    /**
     * 查询组队列表
     * @param teamQuery
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery){
        if(teamQuery == null){
            throw new  BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);//把哪个对象的字段复制到另外一个中
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> Resultpage = teamService.page(page, queryWrapper);
        return ResultUtils.success(Resultpage);

    }


}


















