package com.dhu.ecommercesystem.mapper;
import com.dhu.ecommercesystem.entity.GoodsCategory;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/16-20:58
 */
@Mapper
public interface GoodsCategoryMapper {
    // 增
    int insert(GoodsCategory record);
    int insertSelective(GoodsCategory record);
    // 删
    int deleteByPrimaryKey(@Param("id") Long id);
    int deleteBatch(Long[] ids);
    // 改
    int updateByPrimaryKey(GoodsCategory record);
    int updateSelective(GoodsCategory record);
    // 查
    GoodsCategory selectByPrimaryKey(@Param("id") Long id);

    GoodsCategory selectByLevelAndName(@Param("categoryLevel") Byte categoryLevel, @Param("categoryName") String categoryName);

    List<GoodsCategory> findGoodsCategoryList(PageQueryUtil pageUtil);
    int getTotalGoodsCategories(PageQueryUtil pageUtil);

    //获取某些标签的下一级标签列表
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(@Param("parentIds") List<Long> parentIds, @Param("categoryLevel") int categoryLevel, @Param("number") int number);
}
