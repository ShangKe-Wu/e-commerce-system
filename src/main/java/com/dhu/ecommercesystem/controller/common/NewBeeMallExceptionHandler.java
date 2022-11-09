package com.dhu.ecommercesystem.controller.common;

import com.dhu.ecommercesystem.common.NewBeeMallException;
import com.dhu.ecommercesystem.util.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author:WuShangke
 * @create:2022/7/23-14:04
 * 全局异常处理
 */
@RestControllerAdvice
public class NewBeeMallExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Object handlerException(Exception e, HttpServletRequest request){
        Result result = new Result();
        result.setResultCode(500);
        //判断是否为自定义异常
        if(e instanceof NewBeeMallException){
            result.setMessage(e.getMessage());
        }
        else{
            e.printStackTrace();
            result.setMessage("未知异常");
        }
        //检查是否是Ajax，如果是则返回Result json串，如果不是则返回error视图
        String contentType = request.getHeader("Content-Type");
        String accept = request.getHeader("Accept");
        String XRequestWith = request.getHeader("X-Requested-With");

        if(contentType!=null && contentType.contains("application/json")
        || accept!=null && accept.contains("application/json")
        || "XMLHttpRequest".equalsIgnoreCase(XRequestWith)){
            return result; //是Ajax
        }
        else{
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("message",e.getMessage());
            modelAndView.addObject("url",request.getRequestURL());
            modelAndView.addObject("stackTrace",e.getStackTrace());
            modelAndView.setViewName("error/error");
            return modelAndView;
        }
    }
}
