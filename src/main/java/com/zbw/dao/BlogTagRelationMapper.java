package com.zbw.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbw.entity.BlogTagRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BlogTagRelationMapper extends BaseMapper<BlogTagRelation> {

}
