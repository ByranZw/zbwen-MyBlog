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
public class BlogCategory implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "category_id", type = IdType.AUTO)
    private Integer categoryId;

    @TableField("category_name")
    private String categoryName;

    @TableField("category_icon")
    private String categoryIcon;

    /**
     * 分类等级
     */
    @TableField("category_rank")
    private Integer categoryRank;

    /**
     * 0否1是
     */
    @TableField("delete_status")
    private Integer deleteStatus;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")
    @TableField("create_time")
    private Date createTime;


}
