package com.zbw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zbw.entity.BlogLink;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 友情链接表 服务类
 * </p>
 *
 * @author zbw
 * @since 2021-07-26
 */
public interface BlogLinkService extends IService<BlogLink> {

    List<BlogLink> getAllLinks() throws Exception;

    void setLinkCache() throws Exception;
}
