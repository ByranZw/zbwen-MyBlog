package com.zbw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zbw.config.BeanConfig;
import com.zbw.config.RedisConfig;
import com.zbw.constants.DeleteStatusEnum;
import com.zbw.dao.ConfigMapper;

import com.zbw.entity.BlogCategory;
import com.zbw.entity.Config;
import com.zbw.pojo.dto.BlogPageCondition;
import com.zbw.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Map<String, String> getAllConfigs() throws Exception {

        List<Config> allConfigsList = this.getAllConfigsList();

        Map<String, String> collect = allConfigsList.stream()
                .collect(Collectors.toMap(Config::getConfigField, Config::getConfigValue));

        return collect;
    }

    @Override
    public List<Config> getAllConfigsList() throws Exception{

        List<Config> configList = new ArrayList<>();
        //如果缓存没有
        if (!redisTemplate.hasKey(RedisConfig.REDIS_CONFIG)) {
            configList = new LambdaQueryChainWrapper<>(configMapper).list();

            for (Config config : configList) {
                redisTemplate.opsForList().rightPush(RedisConfig.REDIS_CONFIG, config.getConfigField());
                redisTemplate.opsForValue().set(RedisConfig.REDIS_CONFIG_PREFIX + config.getConfigField()
                        , objectMapper.writeValueAsString(config));
            }

        } else {

            List<String> configField = redisTemplate.opsForList().range(RedisConfig.REDIS_CONFIG, 0, -1);
            for (String field : configField){

                String configJson = redisTemplate.opsForValue().get(RedisConfig.REDIS_CONFIG_PREFIX + field);
                configList.add(objectMapper.readValue(configJson,Config.class));
            }
        }

        return configList;
    }

    @Override
    public void setConfigCache() throws Exception{
        //如果存在，先清除缓存
        if(redisTemplate.hasKey(RedisConfig.REDIS_CONFIG)){

            redisTemplate.delete(RedisConfig.REDIS_CONFIG);
        }

        List<Config> configList = new LambdaQueryChainWrapper<>(configMapper).list();

        for (Config config : configList) {
            redisTemplate.opsForList().rightPush(RedisConfig.REDIS_CONFIG, config.getConfigField());
            redisTemplate.opsForValue().set(RedisConfig.REDIS_CONFIG_PREFIX + config.getConfigField()
                    , objectMapper.writeValueAsString(config));
        }

    }
}
