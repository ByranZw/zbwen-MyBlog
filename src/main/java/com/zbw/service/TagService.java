package com.zbw.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbw.entity.BlogTags;
import com.zbw.pojo.vo.BlogTagCount;

import java.util.List;

public interface TagService extends IService<BlogTags> {

    List<BlogTagCount> getBlogTagCountForIndex();

    boolean clearTag(Integer tagId);
}
