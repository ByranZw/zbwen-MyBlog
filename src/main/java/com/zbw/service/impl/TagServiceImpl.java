package com.zbw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbw.constants.DeleteStatusEnum;
import com.zbw.constants.SysConfigConstants;
import com.zbw.dao.BlogTagMapper;
import com.zbw.entity.Blog;
import com.zbw.entity.BlogTagRelation;
import com.zbw.entity.BlogTags;
import com.zbw.pojo.vo.BlogTagCount;
import com.zbw.service.BlogService;
import com.zbw.service.BlogTagRelationService;
import com.zbw.service.TagService;
import com.zbw.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl extends ServiceImpl<BlogTagMapper, BlogTags> implements TagService {


    @Autowired
    private BlogTagRelationService blogTagRelationService;

    @Autowired
    private BlogService blogService;


    @Override
    public List<BlogTagCount> getBlogTagCountForIndex() {

        List<BlogTags> list = new LambdaQueryChainWrapper<BlogTags>(baseMapper)
                .eq(BlogTags::getDeleteStatus, DeleteStatusEnum.NO_DELETED.getStatus())
                .list();

        AtomicInteger count = new AtomicInteger();

        List<BlogTagCount> blogTagCounts = list.stream().filter(blogTags -> {
            count.set(blogTagRelationService.count(new LambdaQueryWrapper<BlogTagRelation>()
                    .eq(BlogTagRelation::getTagId, blogTags.getTagId())));
            if(count.get() <= 0){
                return false;
            }
            return true;
        }).map(blogTags -> new BlogTagCount()
                                .setTagId(blogTags.getTagId())
                                .setTagName(blogTags.getTagName())
                                .setTagCount(count.get())
                ).sorted().collect(Collectors.toList());

        return blogTagCounts;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean clearTag(Integer tagId) {

        //先删除关联Relation表，再删除Tag表
        blogTagRelationService.remove(new LambdaQueryWrapper<BlogTagRelation>()
                .eq(BlogTagRelation::getTagId,tagId));


        return retBool(baseMapper.deleteById(tagId));
    }
}
