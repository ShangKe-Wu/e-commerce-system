package com.dhu.ecommercesystem.service.impl;

import com.dhu.ecommercesystem.entity.AdminUser;
import com.dhu.ecommercesystem.mapper.AdminUserMapper;
import com.dhu.ecommercesystem.service.AdminUserService;
import com.dhu.ecommercesystem.util.MD5Util;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author:WuShangke
 * @create:2022/7/19-21:31
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {
    @Resource
    AdminUserMapper adminUserMapper;

    @Override
    public AdminUser login(String username, String password) {
        String passwordMD5 = MD5Util.MD5Encode(password,"UTF-8");//密码经过MD5加密
        AdminUser user = adminUserMapper.login(username, passwordMD5);
        return user;
    }

    @Override
    public AdminUser getUserInfoById(Integer id) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(id);
        return adminUser;
    }

    @Override
    public boolean updatePassword(Integer loginUserId, String oldPassword, String newPassword) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        if(adminUser!=null){
            //验证原先密码是否正确
            String password = adminUser.getLoginPassword();//正确的密码，已经经过MD5加密
            String oldPasswordMD5 = MD5Util.MD5Encode(oldPassword,"UTF-8");
            if(password!=null && !password.equals("") && password.equals(oldPasswordMD5)){
                String newPasswordMD5 = MD5Util.MD5Encode(newPassword,"UTF-8");
                adminUser.setLoginPassword(newPasswordMD5);
                if(adminUserMapper.updateByPrimaryKeySelective(adminUser)>0){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean updateName(Integer loginUserId, String loginUsername, String nickName) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        //非空才能更改
        if(adminUser!=null){
            adminUser.setLoginUserName(loginUsername);
            adminUser.setNickName(nickName);
            //更改成功则返回true
            if(adminUserMapper.updateByPrimaryKeySelective(adminUser)>0){
                return true;
            }
        }
        return false;
    }
}
