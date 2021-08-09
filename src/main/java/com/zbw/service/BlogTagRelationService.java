package com.zbw.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbw.entity.Blog;
import com.zbw.entity.BlogTagRelation;

import java.util.List;

public interface BlogTagRelationService extends IService<BlogTagRelation> {

    void removeAndsaveBatch(List<Integer> blogTagIds, Blog blogInfo);
}
