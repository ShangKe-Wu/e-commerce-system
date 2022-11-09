package com.dhu.ecommercesystem.controller.mall;

import com.dhu.ecommercesystem.common.Constants;
import com.dhu.ecommercesystem.common.NewBeeMallException;
import com.dhu.ecommercesystem.entity.GoodsInfo;
import com.dhu.ecommercesystem.entity.vo.GoodsDetailsVO;
import com.dhu.ecommercesystem.entity.vo.SearchPageCategoryVO;
import com.dhu.ecommercesystem.service.CategoryService;
import com.dhu.ecommercesystem.service.GoodsService;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author:WuShangke
 * @create:2022/7/23-15:26
 */
@Controller
public class GoodsController {
    @Resource
    private GoodsService goodsService;
    @Resource
    private CategoryService categoryService;

    @GetMapping({"/search", "/search.html"})
    public String searchPage(@RequestParam Map<String,Object> params, HttpServletRequest request){
        //params就是分页工具pageQueryUtil
        if(StringUtils.isEmpty(params.get("page"))){
            params.put("page",1);
        }
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        //通过分类进行商品的查找，封装分类数据
        if(params.containsKey("goodsCategoryId") && !StringUtils.isEmpty(params.get("goodsCategoryId")+"")){
            Long goodsCategoryId = Long.valueOf(params.get("goodsCategoryId") + "");
            SearchPageCategoryVO categoriesForSearch = categoryService.getCategoriesForSearch(goodsCategoryId);
            if(categoriesForSearch!=null){
                request.setAttribute("searchPageCategoryVO",categoriesForSearch);
                request.setAttribute("goodsCategoryId",goodsCategoryId);
            }
        }
        //封装参数，供前端回显
        if(params.containsKey("orderBy") && !StringUtils.isEmpty(params.get("orderBy")+"")){
            request.setAttribute("orderBy",params.get("orderBy")+"");
        }
        String keyWord="";
        //通过搜索框进行搜索，过滤keyword，去掉空格
        if(params.containsKey("keyword") && !StringUtils.isEmpty( (params.get("keyword")+"").trim())){
            keyWord = params.get("keyword")+"";
        }
        request.setAttribute("keyword",keyWord);
        //搜索的结果必须为上架状态的商品
        params.put("goodsSellStatus",Constants.SELL_STATUS_UP);
        //封装商品数据
        PageQueryUtil pageQueryUtil=new PageQueryUtil(params);
        PageResult goodsPage = goodsService.getSearchGoodsPage(pageQueryUtil);//之前写成后台分页的方法了
        request.setAttribute("pageResult",goodsPage);
        return "mall/search";
    }

    //查看商品详情
    @GetMapping("/goods/detail/{goodsId}")
    public String detailPage(@PathVariable("goodsId") Long goodsId,HttpServletRequest request){
        GoodsDetailsVO goodsDetail = goodsService.getGoodsDetailById(goodsId);
        request.setAttribute("goodsDetail",goodsDetail);
        return "mall/detail";
    }
}
