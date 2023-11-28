package com.whale.lack.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求类
 *
 * @author Erha
 */
@Data
public class PageRequest implements Serializable {
    //使对象序列化保持唯一
    private static final long serialVersionUID = -4162304142710323660L;

    /**
     * 页面大小
     */
    protected int pageSize;
    /**
     * 当前第几页
     */
    protected int pageNum;
}
