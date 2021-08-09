package com.zbw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zbw.config.RedisConfig;
import com.zbw.constants.DeleteStatusEnum;
import com.zbw.entity.BlogLink;
import com.zbw.dao.BlogLinkMapper;
import com.zbw.entity.Config;
import com.zbw.service.BlogLinkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 友情链接表 服务实现类
 * </p>
 *
 * @author zbw
 * @since 2021-07-26
 */
@Service
public class BlogLinkServiceImpl extends ServiceImpl<BlogLinkMapper, BlogLink> implements BlogLinkService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<BlogLink> getAllLinks() throws Exception {

        List<BlogLink> blogLinks = new ArrayList<>();
        if (!redisTemplate.hasKey(RedisConfig.REDIS_LINK)) {
            blogLinks = baseMapper.selectList(new LambdaQueryWrapper<BlogLink>()
                    .eq(BlogLink::getDeleteStatus, DeleteStatusEnum.NO_DELETED.getStatus()));

            for (BlogLink blogLink : blogLinks){
                redisTemplate.opsForList().rightPush(RedisConfig.REDIS_LINK, String.valueOf(blogLink.getLinkId()));
                redisTemplate.opsForValue().set(RedisConfig.REDIS_LINK_PREFIX + blogLink.getLinkId()
                        ,objectMapper.writeValueAsString(blogLink));
            }

        } else {
            List<String> linkIds = redisTemplate.opsForList().range(RedisConfig.REDIS_LINK, 0, -1);
            for (String linkId : linkIds){

                String linkJson = redisTemplate.opsForValue().get(RedisConfig.REDIS_LINK_PREFIX + linkId);
                blogLinks.add(objectMapper.readValue(linkJson, BlogLink.class));
            }
        }

        return blogLinks;
    }

    @Override
    public void setLinkCache() throws Exception{
        //如果存在，先清除缓存
        if(redisTemplate.hasKey(RedisConfig.REDIS_LINK)){

            redisTemplate.delete(RedisConfig.REDIS_LINK);
        }

        List<BlogLink> blogLinks = baseMapper.selectList(new LambdaQueryWrapper<BlogLink>()
                .eq(BlogLink::getDeleteStatus, DeleteStatusEnum.NO_DELETED.getStatus()));

        for (BlogLink blogLink : blogLinks){
            redisTemplate.opsForList().rightPush(RedisConfig.REDIS_LINK, String.valueOf(blogLink.getLinkId()));
            redisTemplate.opsForValue().set(RedisConfig.REDIS_LINK_PREFIX + blogLink.getLinkId()
                    ,objectMapper.writeValueAsString(blogLink));
        }

    }
}
