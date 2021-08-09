package com.zbw.mq;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zbw.config.MailConfig;
import com.zbw.config.RabbitMqConfig;
import com.zbw.dao.BlogMapper;
import com.zbw.entity.Blog;
import com.zbw.entity.BlogComment;
import com.zbw.pojo.dto.MailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RabbitListener(queues = RabbitMqConfig.MAIL_QUEUE)
@Slf4j
public class MailListener {

    //@Autowired注入不进去
    @Resource
    private JavaMailSender mailSender;

    @Autowired
    private MailMessage mailMessage;

    @Autowired
    private BlogMapper blogMapper;


    @RabbitHandler
    public void executeSms(BlogComment blogComment){

        Blog blog = blogMapper.selectOne(
                new LambdaQueryWrapper<Blog>()
                        .select(Blog::getBlogTitle)
                        .eq(Blog::getBlogId,blogComment.getBlogId()));

        if(blogComment.getReplyBody() == null){
            //发送邮件,通知”我“有新评论
            mailSender.send(mailMessage
                    .create(MailConfig.MY_EMAIL
                            , "博客”"+blog.getBlogTitle()+"”有新评论辣"
                            , blogComment.getCommentBody()));
        }else {
            //发送邮件给评论者，通知”他“，”我“回复了
            mailSender.send(mailMessage
                    .create(blogComment.getEmail()
                            ,"您的评论收到回复"
                            ,"您的评论：“"+blogComment.getCommentBody()+"”，作者回复：“"+blogComment.getReplyBody()+"”"));
        }

    }


}
