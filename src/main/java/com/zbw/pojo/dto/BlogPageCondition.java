package com.zbw.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 前台博客分页条件
 * @author Linn-cn
 * @create 2020/12/07
 */
@Data
@Accessors(chain = true)
public class BlogPageCondition {

    /*页码*/
    private Integer pageNum;

    /*每页大小*/
    private Integer pageSize;

    /*搜索关键字*/
    private String keyword;

    /*页名*/
    private String pageName;

    /*标签ID*/
    private String tagId;

    /*分类名称*/
    private String categoryName;

}
