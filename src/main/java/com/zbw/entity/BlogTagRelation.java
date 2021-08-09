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
public class BlogTagRelation implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "relation_id", type = IdType.AUTO)
    private Integer relationId;

    @TableField("blog_id")
    private Long blogId;

    @TableField("tag_id")
    private Integer tagId;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")
    @TableField("create_time")
    private Date createTime;


}
