package com.dhu.ecommercesystem.service;

import com.dhu.ecommercesystem.entity.MallUser;
import com.dhu.ecommercesystem.entity.vo.UserVO;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;

import javax.servlet.http.HttpSession;

/**
 * @author:WuShangke
 * @create:2022/7/22-21:42
 */
public interface UserService {
    // 注册
    String register(String userName,String password);
    // 登录
    String login(String userName, String passwordMD5, HttpSession httpSession);
    // 分页
    PageResult getUserPage(PageQueryUtil pageQueryUtil);
    // 修改信息，并返回修改后的信息
    UserVO updateUserInfo(MallUser user,HttpSession httpSession);
    // 禁用/解禁账号 0:未封 1：已封
    Boolean lockUser(Long ids[],int lockStatus);
}
