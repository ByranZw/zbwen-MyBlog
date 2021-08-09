package com.zbw.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BlogDetailVO {

    private Long blogId;

    private String blogTitle;

    private Integer categoryId;

    private Integer commentCount;

    private String categoryIcon;

    private String categoryName;

    private String blogCoverImage;

    private Long blogViews;

    private List<String> blogTags;

    private String blogContent;

    private Integer commentStatus;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")
    private Date createTime;


}
