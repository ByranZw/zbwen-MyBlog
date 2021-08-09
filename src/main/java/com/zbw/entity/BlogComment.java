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
public class BlogComment implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "comment_id", type = IdType.AUTO)
    private Integer commentId;

    @TableField("comment_body")
    private String commentBody;

    @TableField("blog_id")
    private Long blogId;

    /**
     * 评论者名称
     */
    @TableField("commentator")
    private String commentator;

    @TableField("email")
    private String email;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")
    @TableField("create_time")
    private Date createTime;

    @TableField("commentator_ip")
    private String commentatorIp;

    /**
     * 回复内容
     */
    @TableField("reply_body")
    private String replyBody;

    /**
     * 回复时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")
    @TableField("reply_time")
    private Date replyTime;

    /**
     * 是否通过审核
     */
    @TableField("comment_status")
    private Integer commentStatus;

    /**
     * 是否删除
     */
    @TableField("delete_status")
    private Integer deleteStatus;


}
