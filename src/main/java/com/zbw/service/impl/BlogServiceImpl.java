package com.zbw.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zbw.config.RabbitMqConfig;
import com.zbw.config.RedisConfig;
import com.zbw.constants.BlogStatusEnum;
import com.zbw.constants.DeleteStatusEnum;
import com.zbw.dao.BlogCommentMapper;
import com.zbw.dao.BlogMapper;
import com.zbw.dao.BlogTagMapper;
import com.zbw.dao.BlogTagRelationMapper;
import com.zbw.entity.*;
import com.zbw.pojo.dto.AjaxPutPage;
import com.zbw.pojo.dto.BlogPageCondition;
import com.zbw.pojo.vo.BlogVo;
import com.zbw.pojo.vo.SimpleBlogListVO;
import com.zbw.service.BlogService;
import com.zbw.service.CategoryService;
import com.zbw.util.PageResult;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private BlogTagRelationMapper blogTagRelationMapper;

    @Autowired
    private BlogCommentMapper blogCommentMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public List<SimpleBlogListVO> getNewOrHotBlogs(int newOrHot, Integer current, Integer pageSize) {

        Page<Blog> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Blog> sqlWrapper = null;
        if (newOrHot == 1) {//最新的条件
            sqlWrapper = new LambdaQueryWrapper<Blog>()
                    .select(Blog::getBlogId, Blog::getBlogTitle)//查询返回指定字段
                    .eq(Blog::getBlogStatus, BlogStatusEnum.RELEASE.getStatus())
                    .eq(Blog::getDeleteStatus, DeleteStatusEnum.NO_DELETED.getStatus())
                    .orderByDesc(Blog::getCreateTime);
        } else if (newOrHot == 2) {//最多点击率的条件
            sqlWrapper = new LambdaQueryWrapper<Blog>()
                    .select(Blog::getBlogId, Blog::getBlogTitle)//查询返回指定字段
                    .eq(Blog::getBlogStatus, BlogStatusEnum.RELEASE.getStatus())
                    .eq(Blog::getDeleteStatus, DeleteStatusEnum.NO_DELETED.getStatus())
                    .orderByDesc(Blog::getBlogViews);
        } else {//不会走这一步
            sqlWrapper = new LambdaQueryWrapper<Blog>()
                    .select(Blog::getBlogId, Blog::getBlogTitle)//查询返回指定字段
                    .eq(Blog::getBlogStatus, BlogStatusEnum.RELEASE.getStatus())
                    .eq(Blog::getDeleteStatus, DeleteStatusEnum.NO_DELETED.getStatus());
        }

        blogMapper.selectPage(page, sqlWrapper);
        List<SimpleBlogListVO> simpleBlogListVOS = new ArrayList<>();
        for (Blog blog : page.getRecords()) {
            SimpleBlogListVO simpleBlogListVO = new SimpleBlogListVO();
            BeanUtils.copyProperties(blog, simpleBlogListVO);
            simpleBlogListVOS.add(simpleBlogListVO);
        }

        return simpleBlogListVOS;
    }

    //事务，异常回滚
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean clearBlogInfo(Long blogId) {
        if (SqlHelper.retBool(blogMapper.deleteById(blogId))) {
            QueryWrapper<BlogTagRelation> tagRelationWrapper = new QueryWrapper<>();
            tagRelationWrapper.lambda().eq(BlogTagRelation::getBlogId, blogId);
            blogTagRelationMapper.delete(tagRelationWrapper);
            QueryWrapper<BlogComment> commentWrapper = new QueryWrapper<>();
            commentWrapper.lambda().eq(BlogComment::getBlogId, blogId);
            blogCommentMapper.delete(commentWrapper);
            return true;
        }
        return false;
    }

    /**
     * 获取并封装BLogVo，博客详情，包括categoryName和tag
     *
     * @param page
     * @return
     */
    @Override
    public List<BlogVo> findAndPage(Page<Blog> page) {
        return page.getRecords().stream().map(blog -> {
            BlogVo blog1 = new BlogVo();
            BeanUtils.copyProperties(blog, blog1);
            return blog1.setCategoryName(categoryService.getOne(
                    new LambdaQueryWrapper<BlogCategory>()
                            .eq(BlogCategory::getCategoryId, blog.getCategoryId())).getCategoryName())
                    .setBlogTags(
                            blogTagRelationMapper.selectList(
                                    new LambdaQueryWrapper<BlogTagRelation>()
                                            .eq(BlogTagRelation::getBlogId, blog.getBlogId())
                            )//查出来的是List<BlogTagRelation>
                                    .stream().map(blogTagRelation -> {
                                return blogTagMapper.selectList(
                                        new LambdaQueryWrapper<BlogTags>()
                                                .eq(BlogTags::getTagId, blogTagRelation.getTagId()))
                                        .stream()
                                        .map(blogTags -> blogTags.getTagName())
                                        .collect(Collectors.toList());//到这里是List<String:tagName>
                            }).collect(Collectors.toList())
                                    .stream()
                                    .map(String::valueOf)
                                    .collect(Collectors.joining(","))
                    );
        }).collect(Collectors.toList());
    }


    /**
     * 这个是获取Blog-list的，不需要封装tag
     *
     * @param condition
     * @return
     */
    @Override
    public PageResult findMyBlogPage(BlogPageCondition condition) {

        //如果没有分页数据，设为首页
        if (Objects.isNull(condition.getPageNum())) {
            condition.setPageNum(1);
        }
        if (Objects.isNull(condition.getPageSize())) {
            condition.setPageSize(10);
        }
        //Mybatis-plus 分页的工具类
        Page<Blog> blogPage = new Page<>();
        blogPage.setCurrent(condition.getPageNum());//设置页码
        blogPage.setSize(condition.getPageSize());//设置页大小
        //条件构造器，mybatis-plus封装条件
        LambdaQueryWrapper<Blog> sqlWrapper = new LambdaQueryWrapper<Blog>()
                .eq(Blog::getBlogStatus, BlogStatusEnum.RELEASE.getStatus())
                .eq(Blog::getDeleteStatus, DeleteStatusEnum.NO_DELETED.getStatus());

        if (condition.getKeyword() != null) {
            sqlWrapper.and(blogLambdaQueryWrapper -> blogLambdaQueryWrapper
                    .like(Blog::getBlogTitle, condition.getKeyword())
                    .or()
                    .like(Blog::getBlogPreface, condition.getKeyword()));
        }

        //博客首页有标签可以点，可以通过标签找博客，（热门标签）
        if (Objects.nonNull(condition.getTagId())) {
            List<BlogTagRelation> list = blogTagRelationMapper.selectList(new LambdaQueryWrapper<BlogTagRelation>()
                    .eq(BlogTagRelation::getTagId, condition.getTagId()));
            if (!CollectionUtils.isEmpty(list)) {
                sqlWrapper.in(Blog::getBlogId, list.stream().map(BlogTagRelation::getBlogId).toArray());
            }
        }

        if (Objects.nonNull(condition.getCategoryName())) {
            BlogCategory blogCategory = categoryService.getOne(new LambdaQueryWrapper<BlogCategory>()
                    .eq(BlogCategory::getCategoryName, condition.getCategoryName()));
            if (blogCategory != null) {
                sqlWrapper.eq(Blog::getCategoryId, blogCategory.getCategoryId());
            }
        }
        sqlWrapper.orderByDesc(Blog::getPriority);
        sqlWrapper.orderByDesc(Blog::getCreateTime);

        //mybatis-plus的分页查询，这里执行的sql
        blogMapper.selectPage(blogPage, sqlWrapper);//查询出来的结果存放在blogPage里

        List<BlogVo> blogVoList = blogPage.getRecords().stream().map(blog -> {
            BlogVo blogVo = new BlogVo();
            BeanUtils.copyProperties(blog, blogVo);
            blogVo.setCategoryName(categoryService.getOne(
                    new LambdaQueryWrapper<BlogCategory>()
                            .eq(BlogCategory::getCategoryId, blog.getCategoryId())).getCategoryName()
            );
            return blogVo;
        }).collect(Collectors.toList());

        //封装分页结果
        PageResult blogPageResult = new PageResult(blogVoList, blogPage.getTotal(),
                condition.getPageSize(), condition.getPageNum());

        return blogPageResult;
    }

    /**
     * 专门为首页的缓存，缓存的是blog-list，不包括tag
     *
     * @param condition
     * @return
     */
    @Override
    public PageResult pageIndex(BlogPageCondition condition) throws Exception {
        //如果没有分页数据，设为首页
        if (Objects.isNull(condition.getPageNum())) {
            condition.setPageNum(1);
        }
        if (Objects.isNull(condition.getPageSize())) {
            condition.setPageSize(RedisConfig.REDIS_INDEX_BLOG_COUNT);
        }

        int start = (condition.getPageNum() - 1) * condition.getPageSize();
        //没有缓存 需查询mysql 设置缓存
        if (!redisTemplate.hasKey(RedisConfig.REDIS_INDEX_BLOG)) {
            this.setBlogCache(condition);
        }

        PageResult myBlogPage = new PageResult();
        //第二页开始就不缓存
        if (start >= RedisConfig.REDIS_INDEX_BLOG_COUNT) {
            // 开始位置大于缓存数量 即查询范围不在缓存内 查询mysql 且不设置缓存
            myBlogPage = this.findMyBlogPage(condition);

        } else if (start + condition.getPageSize() > RedisConfig.REDIS_INDEX_BLOG_COUNT) {
            // 查询范围部分在缓存内
            List<String> redisBlogIds = redisTemplate.opsForList().range(RedisConfig.REDIS_INDEX_BLOG, start, RedisConfig.REDIS_INDEX_BLOG_COUNT - 1);
            List blogVoList = new ArrayList<>();
            for (String blogId : redisBlogIds) {
                String blogJson = redisTemplate.opsForValue().get(RedisConfig.REDIS_BLOG_PREFIX + blogId);
                BlogVo blogVo = objectMapper.readValue(blogJson, BlogVo.class);
                blogVoList.add(blogVo);
            }
            myBlogPage = this.findMyBlogPage(new BlogPageCondition().setPageNum(RedisConfig.REDIS_INDEX_BLOG_COUNT).setPageSize(condition.getPageSize() - (RedisConfig.REDIS_INDEX_BLOG_COUNT - start)));

            myBlogPage.setList(Collections.singletonList(blogVoList.addAll(myBlogPage.getList())));
            myBlogPage.setTotalCount(Long.parseLong(redisTemplate.opsForValue().get(RedisConfig.REDIS_BLOG_TOTAL)));

        } else {
            // 查询范围全在缓存
            List<String> redisBlogIds = redisTemplate.opsForList().range(RedisConfig.REDIS_INDEX_BLOG, start, start - 1 + condition.getPageSize());
            List<BlogVo> blogVos = new ArrayList<>();
            for (String blogId : redisBlogIds) {
                String blogJson = redisTemplate.opsForValue().get(RedisConfig.REDIS_BLOG_PREFIX + blogId);
                BlogVo blogVo = objectMapper.readValue(blogJson, BlogVo.class);
                blogVos.add(blogVo);
            }
            myBlogPage.setList(blogVos);
            myBlogPage.setTotalCount(Long.parseLong(redisTemplate.opsForValue().get(RedisConfig.REDIS_BLOG_TOTAL)));
            myBlogPage.setTotalPage(Integer.parseInt(redisTemplate.opsForValue().get(RedisConfig.REDIS_BLOG_TOTAL_PAGE)));

        }

        myBlogPage.setPageSize(condition.getPageSize());
        myBlogPage.setCurrPage(condition.getPageNum());

        return myBlogPage;
    }

    @Override
    public void increseView(Blog blog) {

        blog.setBlogViews(blog.getBlogViews() + 1);
        rabbitTemplate.convertAndSend(RabbitMqConfig.BLOG_QUEUE, blog);
    }

    @Override
    public void setBlogCache(BlogPageCondition condition) throws Exception {

        //如果存在，先清除缓存
        if(redisTemplate.hasKey(RedisConfig.REDIS_INDEX_BLOG)){

            redisTemplate.delete(RedisConfig.REDIS_INDEX_BLOG);
        }
        PageResult myBlogPage = this.findMyBlogPage(condition);
        List<BlogVo> blogVoList = (List<BlogVo>) myBlogPage.getList();
        redisTemplate.opsForValue().set(RedisConfig.REDIS_BLOG_TOTAL, myBlogPage.getTotalCount() + "");
        redisTemplate.opsForValue().set(RedisConfig.REDIS_BLOG_TOTAL_PAGE, myBlogPage.getTotalPage() + "");
        //一个list缓存blogId，并用set缓存每一个BlogVO对象
        for (BlogVo blogVo : blogVoList) {

            String blogId = String.valueOf(blogVo.getBlogId());
            redisTemplate.opsForList().rightPush(RedisConfig.REDIS_INDEX_BLOG, blogId);
            redisTemplate.opsForValue().set(RedisConfig.REDIS_BLOG_PREFIX + blogId
                    , objectMapper.writeValueAsString(blogVo));
        }
    }

    @Override
    public void updateCache(Blog blogInfo) throws Exception{
        // 数据 存在于缓存中
        if (redisTemplate.hasKey(RedisConfig.REDIS_BLOG_PREFIX + blogInfo.getBlogId())) {

            BlogVo blogVo = new BlogVo();
            BeanUtils.copyProperties(blogInfo, blogVo);
            blogVo.setCategoryName(
                    categoryService.getOne(
                            new LambdaQueryWrapper<BlogCategory>()
                                    .eq(BlogCategory::getCategoryId, blogInfo.getCategoryId()))
                            .getCategoryName());


            redisTemplate.opsForValue().set(RedisConfig.REDIS_BLOG_PREFIX + blogVo.getBlogId()
                    , objectMapper.writeValueAsString(blogVo));
        }
    }


}
