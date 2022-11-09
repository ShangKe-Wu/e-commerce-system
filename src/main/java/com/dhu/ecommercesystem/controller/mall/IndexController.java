package com.dhu.ecommercesystem.controller.mall;

import com.dhu.ecommercesystem.common.Constants;
import com.dhu.ecommercesystem.common.IndexConfigTypeEnum;
import com.dhu.ecommercesystem.common.NewBeeMallException;
import com.dhu.ecommercesystem.entity.vo.IndexCarouselVO;
import com.dhu.ecommercesystem.entity.vo.IndexCategoryVO;
import com.dhu.ecommercesystem.entity.vo.IndexConfigGoodsVO;
import com.dhu.ecommercesystem.service.CarouselService;
import com.dhu.ecommercesystem.service.CategoryService;
import com.dhu.ecommercesystem.service.IndexConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/23-17:35
 */
@Controller
public class IndexController {
    @Resource
    CategoryService categoryService;
    @Resource
    CarouselService carouselService;
    @Resource
    IndexConfigService indexConfigService;

    @GetMapping({"/","/index","index.html"})
    public String indexPage(HttpServletRequest request){
        //首页分类
        List<IndexCategoryVO> categories = categoryService.getCategoriesForIndex();
        if(CollectionUtils.isEmpty(categories)){
            NewBeeMallException.fail("分类数据不完善");
        }
        request.setAttribute("categories",categories);
        //轮播图
        List<IndexCarouselVO> carousels = carouselService.getCarouseForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        request.setAttribute("carousels",carousels);
        //热销商品
        List<IndexConfigGoodsVO> hotGoodses = indexConfigService.getConfigGoodsForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        request.setAttribute("hotGoodses",hotGoodses);
        //新品
        List<IndexConfigGoodsVO> newGoodses = indexConfigService.getConfigGoodsForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        request.setAttribute("newGoodses",newGoodses);
        //推荐商品
        List<IndexConfigGoodsVO> recommendGoodses = indexConfigService.getConfigGoodsForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        request.setAttribute("recommendGoodses",recommendGoodses);
        return "mall/index";
    }
}
