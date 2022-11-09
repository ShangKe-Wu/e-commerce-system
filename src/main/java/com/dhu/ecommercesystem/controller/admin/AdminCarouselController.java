package com.dhu.ecommercesystem.controller.admin;

import cn.hutool.core.util.ArrayUtil;
import com.dhu.ecommercesystem.common.ServiceResultEnum;
import com.dhu.ecommercesystem.entity.Carousel;
import com.dhu.ecommercesystem.service.CarouselService;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;
import com.dhu.ecommercesystem.util.Result;
import com.dhu.ecommercesystem.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * @author:WuShangke
 * @create:2022/7/25-15:52
 * 轮播图管理
 */
@Controller
@RequestMapping("/admin")
public class AdminCarouselController {
    @Resource
    CarouselService carouselService;

    //页面跳转
    @GetMapping("/carousels")
    public String carouselPage(HttpServletRequest request){
        request.setAttribute("path","newbee_mall_carousel");
        return "admin/newbee_mall_carousel";
    }

    //获取列表
    @RequestMapping(value = "/carousels/list", method = RequestMethod.GET)
    @ResponseBody
    public Result getList(@RequestParam Map<String,Object> params){
        if(StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))){
            return ResultGenerator.genFailResult("参数异常");
        }
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        PageResult carouselPage = carouselService.getCarouselPage(pageQueryUtil);
        return ResultGenerator.genSuccessResult(carouselPage);
    }

    //添加
    @PostMapping("/carousels/save")
    @ResponseBody
    public Result save(@RequestBody Carousel carousel){
        if (StringUtils.isEmpty(carousel.getCarouselUrl())
                || Objects.isNull(carousel.getCarouselRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = carouselService.saveCarousel(carousel);
        if(result.equals(ServiceResultEnum.SUCCESS.getResult())){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    //修改
    @PostMapping("/carousels/update")
    @ResponseBody
    public Result update( @RequestBody Carousel carousel){
        if (Objects.isNull(carousel.getCarouselId())
                || StringUtils.isEmpty(carousel.getCarouselUrl())
                || Objects.isNull(carousel.getCarouselRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = carouselService.updateCarousel(carousel);
        if(result.equals(ServiceResultEnum.SUCCESS.getResult())){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    //详情
    @GetMapping("/carousels/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Integer id){
        Carousel carousel = carouselService.getCarouselById(id);
        if(carousel==null){
            return ResultGenerator.genFailResult("数据不存在");
        }
        return ResultGenerator.genSuccessResult(carousel);
    }

    //删除
    @PostMapping("/carousels/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer ids[]){
        if(ArrayUtil.isEmpty(ids)){
            return ResultGenerator.genFailResult("数据异常");
        }
        if(carouselService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }
        else{
            return ResultGenerator.genFailResult("删除失败");
        }
    }
}
