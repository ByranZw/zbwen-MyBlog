package com.zbw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.zbw.config.MailConfig;
import com.zbw.dao.AdminUserMapper;
import com.zbw.entity.User;

import com.zbw.service.AdminService;

import com.zbw.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminUserMapper,User> implements AdminService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public User login(String userName, String password) {

        String passwordMd5 = MD5Utils.MD5Encode(password,"UTF-8");

        User user = new LambdaQueryChainWrapper<User>(adminUserMapper)
                .eq(User::getUserName, userName)
                .eq(User::getPassword, passwordMd5)
                .one();

        return user;
    }

    @Override
    public boolean validatePassword(Integer userId, String oldPwd) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>(
                new User().setUserId(userId)
                        .setPassword(MD5Utils.MD5Encode(oldPwd, "UTF-8"))
        );
        User adminUser = adminUserMapper.selectOne(queryWrapper);
        return !StringUtils.isEmpty(adminUser);
    }

    @Transactional
    @Override
    public boolean updateUserInfo(User adminUser) {
        return SqlHelper.retBool(adminUserMapper.updateById(adminUser));
    }



}
