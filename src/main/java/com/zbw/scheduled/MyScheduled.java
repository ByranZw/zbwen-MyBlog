package com.zbw.scheduled;

import com.zbw.config.MailConfig;
import com.zbw.config.RabbitMqConfig;
import com.zbw.pojo.dto.MailMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class MyScheduled {

    @Resource
    private JavaMailSender mailSender;

    @Autowired
    private MailMessage mailMessage;

    @Scheduled(cron = "0 0 9 * * *")
    public void sendMail(){

        String oneS = httpClient.getOneS();

        mailSender.send(mailMessage.create(MailConfig.YT_EMAIL
                ,"宝贝今天也要开心哦！"
                ,oneS
        ));

    }

}
