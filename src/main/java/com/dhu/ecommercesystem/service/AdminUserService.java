package com.dhu.ecommercesystem.service;

import com.dhu.ecommercesystem.entity.AdminUser;

/**
 * @author:WuShangke
 * @create:2022/7/19-21:14
 */
public interface AdminUserService {
    //登录
    AdminUser login(String username,String password);
    //获取登录用户信息
    AdminUser getUserInfoById(Integer id);
    //修改密码
    boolean updatePassword(Integer loginUserId,String oldPassword,String newPassword);
    //修改名称信息
    boolean updateName(Integer loginUserId,String loginUsername,String nickName);
}
