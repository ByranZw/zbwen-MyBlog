package com.zbw.entity;

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
public class Config implements Serializable {

private static final long serialVersionUID=1L;

    @TableId("config_field")
    private String configField;

    @TableField("config_name")
    private String configName;

    @TableField("config_value")
    private String configValue;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")
    @TableField("create_time")
    private Date createTime;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")
    @TableField("update_time")
    private Date updateTime;


}
