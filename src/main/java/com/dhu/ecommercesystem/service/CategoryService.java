package com.dhu.ecommercesystem.service;

import com.dhu.ecommercesystem.entity.GoodsCategory;
import com.dhu.ecommercesystem.entity.vo.IndexCategoryVO;
import com.dhu.ecommercesystem.entity.vo.SearchPageCategoryVO;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;

import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/20-20:19
 */
public interface CategoryService {
    // 增
    String saveCategory(GoodsCategory category);
    // 删
    Boolean deleteBatch(Long []ids);
    // 改
    String updateGoodsCategory(GoodsCategory category);
    // 查
    GoodsCategory getGoodsCategoryById(Long id);
    //后台分页
    PageResult getCategoriesPage(PageQueryUtil pageUtil);
    //首页分类
    List<IndexCategoryVO> getCategoriesForIndex();
    //搜索页面分类
    SearchPageCategoryVO getCategoriesForSearch(Long categoryId);
    //选择number数量的第n级分类（限制父种类id）
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds,int categoryLevel);
}
