package com.zbw.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zbw.entity.BlogCategory;
import com.zbw.pojo.dto.BlogPageCondition;
import com.zbw.util.PageQueryUtil;
import com.zbw.util.PageResult;

import java.util.List;

public interface CategoryService extends IService<BlogCategory> {


    boolean clearCategory(BlogCategory blogCategory);

    List<BlogCategory> getCategoryList() throws Exception;

    void setCategoryCache() throws Exception;
}
