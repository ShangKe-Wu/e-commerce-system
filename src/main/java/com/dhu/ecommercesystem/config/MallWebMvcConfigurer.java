package com.dhu.ecommercesystem.config;

import com.dhu.ecommercesystem.common.Constants;
import com.dhu.ecommercesystem.interceptor.AdminLoginInterceptor;
import com.dhu.ecommercesystem.interceptor.CartNumberInterceptor;
import com.dhu.ecommercesystem.interceptor.MallLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author:WuShangke
 * @create:2022/7/26-15:54
 */
@Configuration
public class MallWebMvcConfigurer implements WebMvcConfigurer {
    @Autowired
    AdminLoginInterceptor adminLoginInterceptor;
    @Autowired
    CartNumberInterceptor cartNumberInterceptor;
    @Autowired
    MallLoginInterceptor mallLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //后台登录拦截
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login")
                .excludePathPatterns("/admin/dist/**")
                .excludePathPatterns("/admin/plugins/**");
        //购物车拦截（购物车数量处理）
        registry.addInterceptor(cartNumberInterceptor)
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/logout");
        //商城用户登录拦截
        registry.addInterceptor(mallLoginInterceptor)
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/logout")
                .addPathPatterns("/goods/detail/**")
                .addPathPatterns("/shop-cart")
                .addPathPatterns("/shop-cart/**")
                .addPathPatterns("/saveOrder")
                .addPathPatterns("/orders")
                .addPathPatterns("/orders/**")
                .addPathPatterns("/personal")
                .addPathPatterns("/personal/updateInfo")
                .addPathPatterns("/selectPayType")
                .addPathPatterns("/payPage")
                .excludePathPatterns("/mall/**");
    }

    //静态资源加载
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + Constants.FILE_UPLOAD_DIC);
        registry.addResourceHandler("/goods-img/**").addResourceLocations("file:" + Constants.FILE_UPLOAD_DIC);
    }
}

