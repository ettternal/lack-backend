//dto业务封装类
package com.whale.lack.service.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.whale.lack.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


/**
 * 队伍查询封装类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    /**
     * id列表
     */
    private List<Long> idList;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 搜索关键词，同时对名称和描述做搜索
     */
    private String searchText;
    /**
     * 队伍名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 最大人数
     */
    private Integer maxNum;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;
}
