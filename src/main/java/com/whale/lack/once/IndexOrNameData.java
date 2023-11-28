package com.whale.lack.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 从excel中提取数据
 */
@Data
@EqualsAndHashCode
public class IndexOrNameData {
    /**
     * 用户id
     */
    @ExcelProperty("a_name")//可以更精确的用列名取匹配
    private String planetCode;

    /**
     * 用户昵称
     */
    @ExcelProperty(index = 1)
    private String username;



}