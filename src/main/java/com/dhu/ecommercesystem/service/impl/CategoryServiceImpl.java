package com.dhu.ecommercesystem.service.impl;

import com.dhu.ecommercesystem.common.CategoryLevelEnum;
import com.dhu.ecommercesystem.common.Constants;
import com.dhu.ecommercesystem.common.ServiceResultEnum;
import com.dhu.ecommercesystem.entity.GoodsCategory;
import com.dhu.ecommercesystem.entity.vo.IndexCategoryVO;
import com.dhu.ecommercesystem.entity.vo.SearchPageCategoryVO;
import com.dhu.ecommercesystem.entity.vo.SecondLevelCategoryVO;
import com.dhu.ecommercesystem.entity.vo.ThirdLevelCategoryVO;
import com.dhu.ecommercesystem.mapper.GoodsCategoryMapper;
import com.dhu.ecommercesystem.service.CategoryService;
import com.dhu.ecommercesystem.util.BeanUtil;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author:WuShangke
 * @create:2022/7/20-20:19
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    GoodsCategoryMapper categoryMapper;

    @Override
    public String saveCategory(GoodsCategory category) {
        GoodsCategory temp = categoryMapper.selectByPrimaryKey(category.getCategoryId());
        if(temp!=null){
            //种类已经存在
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        category.setIsDeleted((byte) 0);
        if(categoryMapper.insertSelective(category)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        int i = categoryMapper.deleteBatch(ids);
        if(i>0){
            return true;
        }
        return false;
    }

    @Override
    public String updateGoodsCategory(GoodsCategory category) {
        GoodsCategory temp1 = categoryMapper.selectByPrimaryKey(category.getCategoryId());
        if(temp1==null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        GoodsCategory temp2 = categoryMapper.selectByLevelAndName(category.getCategoryLevel(), category.getCategoryName());
        if(temp2!=null && !temp2.getCategoryId().equals(temp1.getCategoryId())){
            //同名，同级别 但是id不同，更新失败
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        category.setUpdateTime(new Date());
        if(categoryMapper.updateSelective(category)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public GoodsCategory getGoodsCategoryById(Long id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageResult getCategoriesPage(PageQueryUtil pageUtil) {
        List<GoodsCategory> goodsCategoryList = categoryMapper.findGoodsCategoryList(pageUtil);
        int total = categoryMapper.getTotalGoodsCategories(pageUtil);
        PageResult pageResult = new PageResult(goodsCategoryList,total, pageUtil.getLimit(),pageUtil.getPage());
        return pageResult;
    }

    @Override
    public List<IndexCategoryVO> getCategoriesForIndex() {
        List<IndexCategoryVO> indexCategoryVOS = new ArrayList<>();
        //获取一级标签的数据
        List<GoodsCategory> firstGoodsCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        if(!CollectionUtils.isEmpty(firstGoodsCategories)){
            //获取一级标签的id列表
            List<Long> firstIds = firstGoodsCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            //获取二级标签
            List<GoodsCategory> secondGoodsCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(firstIds, CategoryLevelEnum.LEVEL_TWO.getLevel(), 0);//获取全部
            if(!CollectionUtils.isEmpty(secondGoodsCategories)){
                //获取二级标签的Id列表
                List<Long> secondIds = secondGoodsCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                //获取三级标签
                List<GoodsCategory> thirdGoodsCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(secondIds, CategoryLevelEnum.LEVEL_THREE.getLevel(), 0);
                if(!CollectionUtils.isEmpty(thirdGoodsCategories)){
                    //根据parentId，对三级标签进行分类
                    Map<Long, List<GoodsCategory>> thirdGoodsCategoryMap = thirdGoodsCategories.stream().collect(groupingBy(GoodsCategory::getParentId));
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    //对二级标签进行处理  (就是处理indexVO中的List<SecondLevelCategoryVO> secondLevelCategoryVOS)
                    for(GoodsCategory second :secondGoodsCategories){
                        //封装到VO中
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        //拷贝属性
                        BeanUtil.copyProperties(second,secondLevelCategoryVO);
                        //如果二级标签下还有三级标签，则把三级标签也封装到VO中
                        if(thirdGoodsCategoryMap.containsKey(second.getCategoryId())){
                            List<GoodsCategory> tempCategories = thirdGoodsCategoryMap.get(second.getCategoryId());
                            List<ThirdLevelCategoryVO> thirdLevelCategoryVOS = BeanUtil.copyList(tempCategories, ThirdLevelCategoryVO.class);
                            secondLevelCategoryVO.setThirdLevelCategoryVOS(thirdLevelCategoryVOS);
                        }
                        //把结果添加到List中
                        secondLevelCategoryVOS.add(secondLevelCategoryVO);
                    }
                    //同样对一级标签进行处理
                    if(!CollectionUtils.isEmpty(secondLevelCategoryVOS)){
                        //根据parentId对二级标签进行分类
                        Map<Long, List<SecondLevelCategoryVO>> secondCategoriesMap = secondLevelCategoryVOS.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        for(GoodsCategory firstCategory:firstGoodsCategories){
                            IndexCategoryVO indexCategoryVO = new IndexCategoryVO();
                            //复制属性
                            BeanUtil.copyProperties(firstCategory,indexCategoryVO);
                            //如果某个一级标签含有二级标签，则把二级标签封装
                            if(secondCategoriesMap.containsKey(firstCategory.getCategoryId())){
                                List<SecondLevelCategoryVO> tempSecondCategoryVOS1 = secondCategoriesMap.get(firstCategory.getCategoryId());
                                indexCategoryVO.setSecondLevelCategoryVOS(tempSecondCategoryVOS1);
                            }
                            indexCategoryVOS.add(indexCategoryVO);
                        }
                    }
                }
            }
            return indexCategoryVOS;
        }
        return null;
    }

    @Override
    public SearchPageCategoryVO getCategoriesForSearch(Long categoryId) {
        SearchPageCategoryVO searchPageCategoryVO = new SearchPageCategoryVO();
        GoodsCategory thirdGoodsCategory = categoryMapper.selectByPrimaryKey(categoryId);//第三级别的种类标签
        if(thirdGoodsCategory!=null && thirdGoodsCategory.getCategoryLevel()==(CategoryLevelEnum.LEVEL_THREE.getLevel())){
            //获取二级标签
            GoodsCategory secondGoodsCategory = categoryMapper.selectByPrimaryKey(thirdGoodsCategory.getParentId());
            if(secondGoodsCategory!=null && secondGoodsCategory.getCategoryLevel()==CategoryLevelEnum.LEVEL_TWO.getLevel()){
                //获取二级标签下的三级标签列表
                List<GoodsCategory> thirdGoodsCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondGoodsCategory.getCategoryId()), CategoryLevelEnum.LEVEL_THREE.getLevel(), Constants.SEARCH_CATEGORY_NUMBER);
                //获取一级标签
                GoodsCategory firstGoodsCategory = categoryMapper.selectByPrimaryKey(secondGoodsCategory.getParentId());
                if(firstGoodsCategory!=null && firstGoodsCategory.getCategoryLevel() == CategoryLevelEnum.LEVEL_ONE.getLevel()){
                    //获取一级标签下的二级标签
                    List<GoodsCategory> secondGoodsCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstGoodsCategory.getCategoryId()), CategoryLevelEnum.LEVEL_TWO.getLevel(), Constants.SEARCH_CATEGORY_NUMBER);
                    searchPageCategoryVO.setFirstLevelCategoryName(firstGoodsCategory.getCategoryName());
                    searchPageCategoryVO.setSecondLevelCategoryList(secondGoodsCategories);
                    searchPageCategoryVO.setSecondLevelCategoryName(secondGoodsCategory.getCategoryName());
                    searchPageCategoryVO.setThirdLevelCategoryList(thirdGoodsCategories);
                    searchPageCategoryVO.setCurrentCategoryName(thirdGoodsCategory.getCategoryName());
                    return searchPageCategoryVO;
                }

            }

        }
        return null;
    }

    @Override
    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel) {
        List<GoodsCategory> goodsCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(parentIds, categoryLevel, 0);//0代表所有数据
        return goodsCategories;
    }
}
