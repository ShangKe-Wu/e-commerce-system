package com.dhu.ecommercesystem.controller.mall;

import cn.hutool.captcha.ShearCaptcha;
import com.dhu.ecommercesystem.common.Constants;
import com.dhu.ecommercesystem.common.ServiceResultEnum;
import com.dhu.ecommercesystem.entity.MallUser;
import com.dhu.ecommercesystem.entity.vo.UserVO;
import com.dhu.ecommercesystem.service.UserService;
import com.dhu.ecommercesystem.util.MD5Util;
import com.dhu.ecommercesystem.util.Result;
import com.dhu.ecommercesystem.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author:WuShangke
 * @create:2022/7/24-14:05
 */
@Controller
public class PersonalController {
    @Resource
    UserService userService;

    //个人中心页面
    @GetMapping("/personal")
    public String personalPage(HttpServletRequest request){
        request.setAttribute("path","personal");
        return "mall/personal";
    }
    //退出账户，返回登录页面
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute(Constants.MALL_USER_SESSION_KEY);
        return "mall/login";
    }

    //跳转到登录页面
    @GetMapping({"/login","login.html"})
    public String loginPage(){
        return "mall/login";
    }

    //跳转到注册页面
    @GetMapping("/register")
    public String registerPage(){
        return "mall/register";
    }

    @GetMapping("/personal/addresses")
    public String addressPage(){
        return "mall/address";
    }

    //登录功能
    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestParam("loginName") String loginName,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode,
                        HttpSession session){
        if (StringUtils.isEmpty(loginName)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if(StringUtils.isEmpty(password)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if(StringUtils.isEmpty(verifyCode)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        //检查验证码是否正确
        ShearCaptcha shearCaptcha = (ShearCaptcha) session.getAttribute(Constants.MALL_VERIFY_CODE_KEY);
        if(!shearCaptcha.verify(verifyCode)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        String result = userService.login(loginName, MD5Util.MD5Encode(password,"UTF-8"),session);
        if(result.equals(ServiceResultEnum.SUCCESS.getResult())){
            session.removeAttribute(Constants.MALL_VERIFY_CODE_KEY);
            return ResultGenerator.genSuccessResult();
        }
        else{
            return ResultGenerator.genFailResult(result);
        }
    }

    //注册功能
    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestParam("loginName") String loginName,
                           @RequestParam("password") String password,
                           @RequestParam("verifyCode") String verifyCode,
                           HttpSession session){
        if (StringUtils.isEmpty(loginName)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if(StringUtils.isEmpty(password)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if(StringUtils.isEmpty(verifyCode)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        //检查验证码是否正确
        ShearCaptcha shearCaptcha = (ShearCaptcha) session.getAttribute(Constants.MALL_VERIFY_CODE_KEY);
        if(!shearCaptcha.verify(verifyCode)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        String result = userService.register(loginName, password);
        if(result.equals(ServiceResultEnum.SUCCESS.getResult())){
            session.removeAttribute(Constants.MALL_VERIFY_CODE_KEY);
            return ResultGenerator.genSuccessResult();
        }
        else{
            return ResultGenerator.genFailResult(result);
        }
    }

    //修改信息
    @PostMapping("/personal/updateInfo")
    @ResponseBody
    public Result updateInfo(@RequestBody MallUser user,HttpSession session){
        UserVO userVO = userService.updateUserInfo(user, session);
        if(userVO==null){
            return ResultGenerator.genFailResult("修改失败");
        }
        return ResultGenerator.genSuccessResult();
    }
}
