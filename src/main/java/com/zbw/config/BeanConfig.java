package com.zbw.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;


/**
 * 将一些不方便加@Component注解的类放在此处
 * 加入spring容器
 */

@Component
public class BeanConfig {

    /**
     * spring-boot内置的json工具
     *
     * @return
     */
    @Bean
    public ObjectMapper objectMapper() {

        return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    /**
     * 新建一个用于发送邮件的消息队列
     *
     * @return
     */
    @Bean
    public Queue sendSmsQueue() {
        return new Queue(RabbitMqConfig.MAIL_QUEUE);
    }

    /**
     * 新建一个用于更新博客的消息队列
     *
     * @return
     */
    @Bean
    public Queue updateBlogQueue() {
        return new Queue(RabbitMqConfig.BLOG_QUEUE);
    }

/*
    @Bean
    public JavaMailSender javaMailSender(){
        return new JavaMailSenderImpl();
    }

*/

}
