package com.dhu.ecommercesystem.controller.common;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import com.dhu.ecommercesystem.common.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author:WuShangke
 * @create:2022/7/23-10:10
 */
@Controller
public class CommonController {
    @GetMapping("/common/kaptcha")
    public void defaultCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.setHeader("Cache-Control","no-store");//不缓存
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");//设置为png格式
        //验证马
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(150, 30, 4, 2);
        //存入session
        request.getSession().setAttribute(Constants.DEFAULT_VERIFY_CODE_KEY,shearCaptcha);
        // 输出图片流
        shearCaptcha.write(response.getOutputStream());
    }
    @GetMapping("/common/mall/kaptcha")
    public void mallCaptcha(HttpServletResponse response,HttpServletRequest request) throws Exception{
        //同样不进行缓存
        response.setHeader("Cache-Control","no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");
        //创建验证码
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(110, 40, 4, 2);
        //存入session
        request.getSession().setAttribute(Constants.MALL_VERIFY_CODE_KEY,shearCaptcha);
        //输出图片流
        shearCaptcha.write(response.getOutputStream());
    }
}
