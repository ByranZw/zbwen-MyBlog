package com.zbw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zbw.config.RedisConfig;
import com.zbw.constants.DeleteStatusEnum;
import com.zbw.constants.SysConfigConstants;
import com.zbw.dao.BlogCategoryMapper;
import com.zbw.dao.BlogMapper;

import com.zbw.entity.Blog;
import com.zbw.entity.BlogCategory;
import com.zbw.pojo.dto.BlogPageCondition;
import com.zbw.pojo.vo.BlogVo;
import com.zbw.service.CategoryService;
import com.zbw.util.PageQueryUtil;
import com.zbw.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<BlogCategoryMapper, BlogCategory> implements CategoryService {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    public RedisTemplate<String,String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean clearCategory(BlogCategory blogCategory) {

        Blog blog = new Blog()
                .setCategoryId(Integer.valueOf(SysConfigConstants.DEFAULT_CATEGORY.getConfigField()));
        LambdaUpdateWrapper<Blog> updateWrapper = new LambdaUpdateWrapper<Blog>()
                .eq(Blog::getCategoryId, blogCategory.getCategoryId());

        blogMapper.update(blog,updateWrapper);

        return retBool(baseMapper.deleteById(blogCategory.getCategoryId()));
    }

    @Override
    public List<BlogCategory> getCategoryList() throws Exception {
        List<BlogCategory> blogCategories = new ArrayList<>();
        //如果redis中没有分类目录的key，则查mysql
        if(!redisTemplate.hasKey(RedisConfig.REDIS_CATEGORY)){
            blogCategories = baseMapper.selectList(
                    new LambdaQueryWrapper<BlogCategory>()
                            .eq(BlogCategory::getDeleteStatus, DeleteStatusEnum.NO_DELETED.getStatus())
                            .orderByDesc(BlogCategory::getCategoryRank));

            for (BlogCategory blogCategory : blogCategories){

                redisTemplate.opsForList().rightPush(RedisConfig.REDIS_CATEGORY, String.valueOf(blogCategory.getCategoryId()));
                redisTemplate.opsForValue().set(RedisConfig.REDIS_CATEGORY_PREFIX+blogCategory.getCategoryId()
                        ,objectMapper.writeValueAsString(blogCategory));
            }

        }else {
            List<String> categoryIds = redisTemplate.opsForList().range(RedisConfig.REDIS_CATEGORY, 0, -1);
            for (String categoryId : categoryIds) {

                String categoryJson =  redisTemplate.opsForValue().get(RedisConfig.REDIS_CATEGORY_PREFIX + categoryId);
                blogCategories.add(objectMapper.readValue(categoryJson,BlogCategory.class));
            }
        }


        return blogCategories;
    }

    @Override
    public void setCategoryCache() throws Exception{

        //如果存在，先清除缓存
        if(redisTemplate.hasKey(RedisConfig.REDIS_CATEGORY)){

            redisTemplate.delete(RedisConfig.REDIS_CATEGORY);
        }

        List<BlogCategory> blogCategories = baseMapper.selectList(
                new LambdaQueryWrapper<BlogCategory>()
                        .eq(BlogCategory::getDeleteStatus, DeleteStatusEnum.NO_DELETED.getStatus())
                        .orderByDesc(BlogCategory::getCategoryRank));

        for (BlogCategory blogCategory : blogCategories){

            redisTemplate.opsForList().rightPush(RedisConfig.REDIS_CATEGORY, String.valueOf(blogCategory.getCategoryId()));
            redisTemplate.opsForValue().set(RedisConfig.REDIS_CATEGORY_PREFIX+blogCategory.getCategoryId()
                    ,objectMapper.writeValueAsString(blogCategory));
        }
    }


}
