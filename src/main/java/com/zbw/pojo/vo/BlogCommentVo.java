package com.zbw.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BlogCommentVo {

    private String blogTitle;

    private String commentator;

    private String email;

    private String commentBody;

    private Date createTime;

    private String replyBody;

    private Date replyTime;

    private byte commentStatus;

    private byte deleteStatus;

    private Integer commentId;

}
