package com.zbw.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbw.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminUserMapper  extends BaseMapper<User> {


}
