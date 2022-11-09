package com.dhu.ecommercesystem.controller.admin;

import com.dhu.ecommercesystem.common.CategoryLevelEnum;
import com.dhu.ecommercesystem.common.Constants;
import com.dhu.ecommercesystem.common.NewBeeMallException;
import com.dhu.ecommercesystem.common.ServiceResultEnum;
import com.dhu.ecommercesystem.entity.GoodsCategory;
import com.dhu.ecommercesystem.entity.GoodsInfo;
import com.dhu.ecommercesystem.service.CategoryService;
import com.dhu.ecommercesystem.service.GoodsService;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.Result;
import com.dhu.ecommercesystem.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author:WuShangke
 * @create:2022/7/25-18:20
 */
@Controller
@RequestMapping("/admin")
public class AdminGoodsController {
    @Resource
    GoodsService goodsService;
    @Resource
    CategoryService categoryService;

    //跳转到后台管理 商品页面
    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request){
        request.setAttribute("path", "newbee_mall_goods");
        return "admin/newbee_mall_goods";
    }

    //跳转到编辑商品页面(添加商品)
    @GetMapping("/goods/edit")
    public String editPage(HttpServletRequest request){
        request.setAttribute("path", "edit");
        //获取商品分类列表
        //先获取一级标签
        List<GoodsCategory> first = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel());
        if(!CollectionUtils.isEmpty(first)){
            //获取二级标签（一级标签的第一个分类下的所有二级标签）
            List<GoodsCategory> second = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(first.get(0).getCategoryId()), CategoryLevelEnum.LEVEL_TWO.getLevel());
            if(!CollectionUtils.isEmpty(second)){
                //获取三级标签（二级标签的第一个分类下的所有三级标签）
                List<GoodsCategory> third = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(second.get(0).getCategoryId()), CategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", first);
                request.setAttribute("secondLevelCategories", second);
                request.setAttribute("thirdLevelCategories", third);
                request.setAttribute("path", "goods-edit");
                return "admin/newbee_mall_goods_edit";
            }
        }
        NewBeeMallException.fail("分类数据不完善");
        return null;
    }

    // 修改商品页面
    @GetMapping("/goods/edit/{goodsId}")
    public String edit(@PathVariable("goodsId") Long goodsId,HttpServletRequest request){
        request.setAttribute("path", "edit");
        GoodsInfo goods = goodsService.getGoodsById(goodsId);
        if(goods.getGoodsCategoryId()!=null){
            if(goods.getGoodsCategoryId()>0){
                //有分类字段则查询相关分类数据返回给前端以供分类的三级联动显示
                GoodsCategory category = categoryService.getGoodsCategoryById(goods.getGoodsCategoryId());
                //商品表中存储的分类id字段为三级分类的id，不为三级分类则是错误数据
                if(category!=null && category.getCategoryLevel()==CategoryLevelEnum.LEVEL_THREE.getLevel()){
                    //查询所有的一级分类
                    List<GoodsCategory> firstLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel());
                    //根据parentId查询当前parentId下所有的三级分类
                    List<GoodsCategory> thirdLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(category.getParentId()),CategoryLevelEnum.LEVEL_THREE.getLevel());
                    //查询当前三级分类的父级二级分类
                    GoodsCategory secondCategory = categoryService.getGoodsCategoryById(category.getParentId());
                    if (secondCategory != null) {
                        //根据parentId查询当前parentId下所有的二级分类
                        List<GoodsCategory> secondLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getParentId()), CategoryLevelEnum.LEVEL_TWO.getLevel());
                        //查询当前二级分类的父级一级分类
                        GoodsCategory firestCategory = categoryService.getGoodsCategoryById(secondCategory.getParentId());
                        if (firestCategory != null) {
                            //所有分类数据都得到之后放到request对象中供前端读取
                            request.setAttribute("firstLevelCategories", firstLevelCategories);
                            request.setAttribute("secondLevelCategories", secondLevelCategories);
                            request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId", firestCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId", secondCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId", category.getCategoryId());
                        }
                    }
                }
            }
        }
        if ( goods.getGoodsCategoryId() == 0) {
            //查询所有的一级分类
            List<GoodsCategory> firstLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                //查询一级分类列表中第一个实体的所有二级分类
                List<GoodsCategory> secondLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), CategoryLevelEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    //查询二级分类列表中第一个实体的所有三级分类
                    List<GoodsCategory> thirdLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), CategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }
        request.setAttribute("goods", goods);
        request.setAttribute("path", "goods-edit");
        return "admin/newbee_mall_goods_edit";
    }

    // 商品列表
    @GetMapping("/goods/list")
    @ResponseBody
    public Result list(@RequestParam Map<String,Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(goodsService.getGoodsPage(pageUtil));
    }

    // 添加商品
    @PostMapping("/goods/save")
    @ResponseBody
    public Result save(@RequestBody GoodsInfo goods){
        if (StringUtils.isEmpty(goods.getGoodsName())
                || StringUtils.isEmpty(goods.getGoodsIntro())
                || StringUtils.isEmpty(goods.getTag())
                || Objects.isNull(goods.getOriginalPrice())
                || Objects.isNull(goods.getGoodsCategoryId())
                || Objects.isNull(goods.getSellingPrice())
                || Objects.isNull(goods.getStockNum())
                || Objects.isNull(goods.getGoodsSellStatus())
                || StringUtils.isEmpty(goods.getGoodsCoverImg())
                || StringUtils.isEmpty(goods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = goodsService.saveGoods(goods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }
    // 修改商品
    @ResponseBody
    @PostMapping("/goods/update")
    public Result update(@RequestBody GoodsInfo goods){
        if (StringUtils.isEmpty(goods.getGoodsName())
                || StringUtils.isEmpty(goods.getGoodsIntro())
                || StringUtils.isEmpty(goods.getTag())
                || Objects.isNull(goods.getOriginalPrice())
                || Objects.isNull(goods.getGoodsCategoryId())
                || Objects.isNull(goods.getSellingPrice())
                || Objects.isNull(goods.getStockNum())
                || Objects.isNull(goods.getGoodsSellStatus())
                || StringUtils.isEmpty(goods.getGoodsCoverImg())
                || StringUtils.isEmpty(goods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = goodsService.updateGoodsInfo(goods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    //获取商品详情
    @GetMapping("/goods/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id){
        GoodsInfo goods = goodsService.getGoodsById(id);
        return ResultGenerator.genSuccessResult(goods);
    }

    // 上架商品
    // 下架商品
    @PutMapping("/goods/status/{status}")
    @ResponseBody
    public Result delete(@PathVariable("status") Integer status,@RequestBody Long ids[]){
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (status != Constants.SELL_STATUS_UP && status != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (goodsService.batchUpdateSellStatus(ids, status)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }
}
