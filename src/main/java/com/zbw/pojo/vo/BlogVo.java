package com.zbw.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BlogVo implements Serializable {


    private Long blogId;

    private String blogTitle;

    private String subUrl;

    private String coverImage;

    private String blogContent;

    /**
     * 博客分类id
     */
    private Integer categoryId;

    private String categoryName;

    private String blogTags;

    /**
     * 0-草稿 1-发布
     */
    private Integer blogStatus;

    /**
     * 阅读量
     */
    private Long blogViews;

    /**
     * 1-允许评论 0-不允许评论
     */
    private Integer commentStatus;

    /**
     * 是否删除 0=否 1=是
     */
    private Integer deleteStatus;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 博客前言
     */
    private String blogPreface;

    private Integer priority;

}
