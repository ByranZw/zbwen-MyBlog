package com.zbw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zbw.entity.Config;
import com.zbw.pojo.dto.BlogPageCondition;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ConfigService extends IService<Config> {

    Map<String,String> getAllConfigs() throws Exception;

    List<Config> getAllConfigsList() throws Exception;

    void setConfigCache() throws Exception;
}
