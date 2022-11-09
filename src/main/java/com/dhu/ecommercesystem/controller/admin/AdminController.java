package com.dhu.ecommercesystem.controller.admin;

import cn.hutool.captcha.ShearCaptcha;
import com.dhu.ecommercesystem.common.Constants;
import com.dhu.ecommercesystem.entity.AdminUser;
import com.dhu.ecommercesystem.service.AdminUserService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author:WuShangke
 * @create:2022/7/25-14:49
 */

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Resource
    AdminUserService adminUserService;

    //跳转登录页面
    @GetMapping("/login")
    public String loginPage(){
        return "admin/login";
    }
    //跳转到后台管理系统的首页
    @GetMapping({"/","/index","/index.html"})
    public String indexPage(HttpServletRequest request){
        request.setAttribute("path","index");
        return "admin/index";
    }

    //登录功能
    @PostMapping("/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode,
                        HttpSession session){
        if(StringUtils.isEmpty(userName)){
            session.setAttribute("errorMsg","用户名不能为空");
            return "admin/login";
        }
        if(StringUtils.isEmpty(password)){
            session.setAttribute("errorMsg","密码不能为空");
            return "admin/login";
        }
        if(StringUtils.isEmpty(verifyCode)){
            session.setAttribute("errorMsg","验证码不能为空");
            return "admin/login";
        }
        //判断验证码是否正确
        ShearCaptcha shearCaptcha=(ShearCaptcha) session.getAttribute(Constants.DEFAULT_VERIFY_CODE_KEY);
        //如果错误
        if(shearCaptcha==null || !shearCaptcha.verify(verifyCode)){
            session.setAttribute("errorMsg","验证码错误");
            return "admin/login";
        }
        //正确则登录
        AdminUser adminUser = adminUserService.login(userName, password);
        if(adminUser!=null){
            session.setAttribute("loginUser",adminUser.getNickName());
            session.setAttribute("loginUserId",adminUser.getAdminUserId());
            return "redirect:/admin/index";
        }
        else{
            session.setAttribute("errorMsg","用户不存在，请检查用户名和密码");
            return "admin/login";
        }
    }

    //跳转到profile页面
    @GetMapping("/profile")
    public String profile(HttpServletRequest request){
        Integer loginUserId=(Integer) request.getSession().getAttribute("loginUserId");
        AdminUser user = adminUserService.getUserInfoById(loginUserId);
        if(user==null){
            return "admin/login";
        }
        request.setAttribute("path","profile");
        request.setAttribute("loginUserName",user.getLoginUserName());
        request.setAttribute("nickName",user.getNickName());
        return "admin/profile";
    }

    //更改密码
    @PostMapping("/profile/password")
    @ResponseBody
    public String passwordUpdate(HttpServletRequest request, @RequestParam("originalPassword") String originalPassword,
                                 @RequestParam("newPassword") String newPassword){
        if(StringUtils.isEmpty(originalPassword) || StringUtils.isEmpty(newPassword)){
            return "密码参数不能为空";
        }
        Integer loginUserId=(Integer) request.getSession().getAttribute("loginUserId");
        if(adminUserService.updatePassword(loginUserId,originalPassword,newPassword)){
            request.getSession().removeAttribute("loginUserId");
            request.getSession().removeAttribute("loginUser");
            request.getSession().removeAttribute("errorMsg");
            return "success";
        }
        return "修改失败，请检查原密码是否正确";
    }

    //修改名字
    @PostMapping("/profile/name")
    @ResponseBody
    public String updateUserName(@RequestParam("loginUserName") String loginUserName,
                                 @RequestParam("nickName") String nickName,
                                 HttpServletRequest request){
        if(StringUtils.isEmpty(loginUserName) || StringUtils.isEmpty(nickName)){
            return "参数不能为空";
        }
        Integer loginUserId=(Integer) request.getSession().getAttribute("loginUserId");
        if(adminUserService.updateName(loginUserId,loginUserName,nickName)){
            return "success";
        }
        return "修改失败";
    }

    //登出账号
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("loginUserId");
        session.removeAttribute("loginUser");
        session.removeAttribute("errorMsg");
        return "admin/login";
    }
}
