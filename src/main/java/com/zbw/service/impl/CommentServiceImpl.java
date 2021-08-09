package com.zbw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbw.config.MailConfig;
import com.zbw.config.RabbitMqConfig;
import com.zbw.dao.BlogCommentMapper;
import com.zbw.dao.BlogMapper;
import com.zbw.entity.Blog;
import com.zbw.entity.BlogComment;
import com.zbw.pojo.dto.AjaxPutPage;
import com.zbw.pojo.vo.BlogCommentVo;
import com.zbw.service.CommentService;
import com.zbw.util.PageQueryUtil;
import com.zbw.util.PageResult;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl extends ServiceImpl<BlogCommentMapper, BlogComment> implements CommentService {

    @Autowired
    private BlogCommentMapper blogCommentMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public List<BlogCommentVo> findAndPage(AjaxPutPage<BlogCommentVo> ajaxPutPage,Long blogId){

        HashMap<String, Object> map = new HashMap<>();
        map.put("start",ajaxPutPage.getStart());
        map.put("limit",ajaxPutPage.getLimit());
        map.put("blogId",blogId);
        return blogCommentMapper.findAndPage(map);
    }

    @Override
    public void sendMail(BlogComment blogComment) {


        //以直接模式存入消息队列
        rabbitTemplate.convertAndSend(RabbitMqConfig.MAIL_QUEUE, blogComment);

    }


}
