package com.zbw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbw.dao.BlogTagRelationMapper;
import com.zbw.entity.Blog;
import com.zbw.entity.BlogTagRelation;
import com.zbw.service.BlogTagRelationService;
import com.zbw.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogTagRelationServiceImpl extends ServiceImpl<BlogTagRelationMapper, BlogTagRelation> implements BlogTagRelationService {


    @Autowired
    private BlogTagRelationMapper blogTagRelationMapper;


    @Override
    public void removeAndsaveBatch(List<Integer> blogTagIds, Blog blogInfo) {
        Long blogId = blogInfo.getBlogId();

        List<BlogTagRelation> list = blogTagIds.stream().map(
                blogTagId -> new BlogTagRelation()
                        .setTagId(blogTagId)
                        .setBlogId(blogId))
                .collect(Collectors.toList());
        blogTagRelationMapper.delete(new LambdaQueryWrapper<BlogTagRelation>()
                .eq(BlogTagRelation::getBlogId, blogInfo.getBlogId()));
        for (BlogTagRelation item : list) {
            item.setCreateTime(DateUtils.getLocalCurrentDate());
            blogTagRelationMapper.insert(item);
        }
    }
}

