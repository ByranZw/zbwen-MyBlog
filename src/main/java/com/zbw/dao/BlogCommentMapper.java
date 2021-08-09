package com.zbw.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbw.entity.BlogComment;
import com.zbw.pojo.dto.AjaxPutPage;
import com.zbw.pojo.vo.BlogCommentVo;
import com.zbw.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Mapper
public interface BlogCommentMapper extends BaseMapper<BlogComment> {

    List<BlogCommentVo> findAndPage(Map map);
}
