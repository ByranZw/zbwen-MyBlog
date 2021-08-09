package com.zbw.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zbw.entity.Blog;
import com.zbw.pojo.dto.AjaxPutPage;
import com.zbw.pojo.dto.BlogPageCondition;
import com.zbw.pojo.vo.BlogVo;
import com.zbw.pojo.vo.SimpleBlogListVO;
import com.zbw.util.PageQueryUtil;
import com.zbw.util.PageResult;

import java.io.IOException;
import java.util.List;

public interface BlogService extends IService<Blog> {


    /**
     * 分页查询博客
     * 查最新（按时间降序）
     * 查最热（按点击率降序）
     * @param current
     * @param size
     * @return
     */
    List<SimpleBlogListVO> getNewOrHotBlogs(int newOrHot,Integer current,Integer size);

    boolean clearBlogInfo(Long blogId);

    List<BlogVo> findAndPage(Page<Blog> page);

    PageResult findMyBlogPage(BlogPageCondition condition);

    PageResult pageIndex(BlogPageCondition index) throws Exception;

    void increseView(Blog blog);

    void setBlogCache(BlogPageCondition condition) throws Exception;

    void updateCache(Blog blogInfo) throws Exception;
}
