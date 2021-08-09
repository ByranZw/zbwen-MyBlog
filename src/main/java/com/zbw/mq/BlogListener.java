package com.zbw.mq;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zbw.config.RabbitMqConfig;
import com.zbw.entity.Blog;
import com.zbw.service.BlogService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 更新博客的队列消费者
 *
 * @blame mqpearh
 */
@Component
@RabbitListener(queues = RabbitMqConfig.BLOG_QUEUE)
public class BlogListener {

    @Autowired
    private BlogService blogService;

    @RabbitHandler
    public void updateBlog(Blog blog) {

        blogService.updateById(blog);
    }
}
