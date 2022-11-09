package com.dhu.ecommercesystem.mapper;

import com.dhu.ecommercesystem.entity.IndexConfig;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/17-21:11
 */
@Mapper
public interface IndexConfigMapper {
    // 增
    int insert(IndexConfig record);
    int insertSelective(IndexConfig record);
    // 删
    int deleteByPrimaryKey(Long configId);
    int deleteBatch(Long[] ids);
    // 改
    int updateByPrimaryKeySelective(IndexConfig record);
    int updateByPrimaryKey(IndexConfig record);
    // 查
    IndexConfig selectByPrimaryKey(Long configId);
    IndexConfig selectByTypeAndGoodsId(@Param("configType") int configType, @Param("goodsId") Long goodsId);

    List<IndexConfig> findIndexConfigList(PageQueryUtil pageUtil);
    int getTotalIndexConfigs(PageQueryUtil pageUtil);

    List<IndexConfig> findIndexConfigsByTypeAndNum(@Param("configType") int configType, @Param("number") int number);
}
