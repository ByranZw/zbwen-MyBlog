package com.zbw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zbw
 * @since 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Blog implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "blog_id", type = IdType.AUTO)
    private Long blogId;

    @TableField("blog_title")
    private String blogTitle;

    @TableField("sub_url")
    private String subUrl;

    @TableField("cover_image")
    private String coverImage;

    @TableField("blog_content")
    private String blogContent;

    /**
     * 博客分类id
     */
    @TableField("category_id")
    private Integer categoryId;

    /**
     * 0-草稿 1-发布
     */
    @TableField("blog_status")
    private Integer blogStatus;

    /**
     * 阅读量
     */
    @TableField("blog_views")
    private Long blogViews;

    /**
     * 1-允许评论 0-不允许评论
     */
    @TableField("comment_status")
    private Integer commentStatus;

    /**
     * 是否删除 0=否 1=是
     */
    @TableField("delete_status")
    private Integer deleteStatus;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")
    @TableField("create_time")
    private Date createTime;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")
    @TableField("update_time")
    private Date updateTime;

    /**
     * 博客前言
     */
    @TableField("blog_preface")
    private String blogPreface;

    /**
     * 置顶优先级
     */
    @TableField("priority")
    private Integer priority;

}
