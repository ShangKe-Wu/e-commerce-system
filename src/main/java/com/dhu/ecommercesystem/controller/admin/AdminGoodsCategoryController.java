package com.dhu.ecommercesystem.controller.admin;

import cn.hutool.core.util.ArrayUtil;
import com.dhu.ecommercesystem.common.CategoryLevelEnum;
import com.dhu.ecommercesystem.common.NewBeeMallException;
import com.dhu.ecommercesystem.common.ServiceResultEnum;
import com.dhu.ecommercesystem.entity.GoodsCategory;
import com.dhu.ecommercesystem.service.CategoryService;
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
import java.util.*;

/**
 * @author:WuShangke
 * @create:2022/7/25-16:44
 */
@Controller
@RequestMapping("/admin")
public class AdminGoodsCategoryController {
    @Resource
    CategoryService categoryService;

    //页面跳转
    @GetMapping("/categories")
    public String categoryPage(HttpServletRequest request,
                               @RequestParam("categoryLevel") Byte categoryLevel,
                               @RequestParam("parentId") Long parentId,
                               @RequestParam("backParentId")Long backParentId){
        if (categoryLevel == null || categoryLevel < 1 || categoryLevel > 3) {
            NewBeeMallException.fail("参数异常");
        }
        request.setAttribute("path", "newbee_mall_category");
        request.setAttribute("categoryLevel",categoryLevel);
        request.setAttribute("parentId",parentId);
        request.setAttribute("backParentId",backParentId);
        return "admin/newbee_mall_category";
    }

    //分页信息，查看列表
    @ResponseBody
    @GetMapping("/categories/list")
    public Result list(@RequestParam Map<String,Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit")) || StringUtils.isEmpty(params.get("categoryLevel")) || StringUtils.isEmpty(params.get("parentId"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageQueryUtil= new PageQueryUtil(params);
        PageResult categoriesPage = categoryService.getCategoriesPage(pageQueryUtil);
        return ResultGenerator.genSuccessResult(categoriesPage);
    }

    //搜索列表 goods_edit.js
    @ResponseBody
    @GetMapping("/categories/listForSelect")
    public Result listForSelect(@RequestParam("categoryId") Long categoryId){
        if (categoryId == null || categoryId < 1) {
            return ResultGenerator.genFailResult("缺少参数！");
        }
        GoodsCategory category = categoryService.getGoodsCategoryById(categoryId);
        //既不是一级分类也不是二级分类则为不返回数据
        if (category == null || category.getCategoryLevel() == CategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        Map categoryResult = new HashMap(4);
        //如果是一级标签，则获取该变标签下的二级标签，和二级标签下的三级标签
        if(category.getCategoryLevel() == CategoryLevelEnum.LEVEL_ONE.getLevel()){
            //查询二级
            List<GoodsCategory> secondCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(category.getCategoryId()), CategoryLevelEnum.LEVEL_TWO.getLevel());
            //查询三级
            if(!CollectionUtils.isEmpty(secondCategories)){
                List<GoodsCategory> thirdCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategories.get(0).getCategoryId()), CategoryLevelEnum.LEVEL_THREE.getLevel());
                categoryResult.put("secondLevelCategories",secondCategories);
                categoryResult.put("thirdLevelCategories",thirdCategories);
            }
        }
        //如果是二级标签，则获取该标签下的三级标签
        if(category.getCategoryLevel() == CategoryLevelEnum.LEVEL_TWO.getLevel()){
            List<GoodsCategory> thirdCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(category.getCategoryId()), CategoryLevelEnum.LEVEL_THREE.getLevel());
            categoryResult.put("thirdLevelCategories",thirdCategories);
        }
        return ResultGenerator.genSuccessResult(categoryResult);
    }

    // 增加
    @PostMapping("/categories/save")
    @ResponseBody
    public Result save(@RequestBody GoodsCategory goodsCategory){
        if (Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getParentId())
                || Objects.isNull(goodsCategory.getCategoryRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = categoryService.saveCategory(goodsCategory);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    // 修改
    @PostMapping("/categories/update")
    @ResponseBody
    public Result update(@RequestBody GoodsCategory goodsCategory){
        if (Objects.isNull(goodsCategory.getCategoryId())
                || Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getParentId())
                || Objects.isNull(goodsCategory.getCategoryRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = categoryService.updateGoodsCategory(goodsCategory);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    // 获取详情
    @GetMapping("/categories/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id){
        GoodsCategory goodsCategory = categoryService.getGoodsCategoryById(id);
        if(goodsCategory==null){
            return ResultGenerator.genFailResult("参数异常，未查询到结果");
        }
        return ResultGenerator.genSuccessResult(goodsCategory);
    }

    // 删除
    @PostMapping("/categories/delete")
    @ResponseBody
    public Result delete(@RequestBody Long ids[]){
        if(ArrayUtil.isEmpty(ids)){
            return ResultGenerator.genFailResult("参数异常");
        }
        if(categoryService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("删除失败");
    }
}
