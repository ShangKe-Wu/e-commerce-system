package com.dhu.ecommercesystem.controller.admin;

import com.dhu.ecommercesystem.service.UserService;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;
import com.dhu.ecommercesystem.util.Result;
import com.dhu.ecommercesystem.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author:WuShangke
 * @create:2022/7/26-15:07
 */
@Controller
@RequestMapping("/admin")
public class AdminMallUserController {
    @Resource
    UserService userService;

    //页面跳转
    @GetMapping("/users")
    public String userPage(HttpServletRequest request){
        request.setAttribute("path","users");
        return "admin/newbee_mall_user";
    }

    //获取列表
    @GetMapping("/users/list")
    @ResponseBody
    public Result list(@RequestParam Map<String,Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        PageResult userPage = userService.getUserPage(pageQueryUtil);
        return ResultGenerator.genSuccessResult(userPage);
    }

    //修改账号状态（封号，解封）
    @PostMapping("/users/lock/{lockStatus}")
    @ResponseBody
    public Result UserState(@RequestBody Long ids[],@PathVariable("lockStatus") Integer lockStatus){
        if(ids.length<1){
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (lockStatus != 0 && lockStatus != 1) {
            return ResultGenerator.genFailResult("操作非法！");
        }
        Boolean result = userService.lockUser(ids, lockStatus);
        if(result){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("修改状态失败!");
    }
}
