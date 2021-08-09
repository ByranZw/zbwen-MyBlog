package com.zbw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbw.entity.User;


public interface AdminService extends IService<User> {

    /**
     * 登陆验证
     * @param username
     * @param password
     * @return
     */
    User login(String username, String password);

    /**
     * 验证密码
     * @param userId
     * @param oldPwd
     * @return
     */
    boolean validatePassword(Integer userId, String oldPwd);

    /**
     * 更新用户信息
     * @param adminUser
     * @return
     */
    boolean updateUserInfo(User adminUser);

}
