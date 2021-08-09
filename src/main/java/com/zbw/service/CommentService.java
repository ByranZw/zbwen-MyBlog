package com.zbw.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbw.entity.BlogComment;
import com.zbw.pojo.dto.AjaxPutPage;
import com.zbw.pojo.vo.BlogCommentVo;

import java.util.List;

public interface CommentService extends IService<BlogComment> {

    List<BlogCommentVo> findAndPage(AjaxPutPage<BlogCommentVo> ajaxPutPage, Long blogId);

    void sendMail(BlogComment blogComment);
}
