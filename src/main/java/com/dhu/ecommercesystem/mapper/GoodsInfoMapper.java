package com.dhu.ecommercesystem.mapper;

import com.dhu.ecommercesystem.entity.GoodsInfo;
import com.dhu.ecommercesystem.entity.StockNumDTO;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/16-21:52
 */
@Mapper
public interface GoodsInfoMapper {
    // 增
    int insert(GoodsInfo record);
    int insertSelective(GoodsInfo record);
    int batchInsert(@Param("newBeeMallGoodsList") List<GoodsInfo> newBeeMallGoodsList);
    // 删
    int deleteByPrimaryKey(Long goodsId);
    // 改
    int updateByPrimaryKey(GoodsInfo record);//没有添加商品详情
    int updateByPrimaryKeySelective(GoodsInfo record);
    int updateByPrimaryKeyWithBLOBs(GoodsInfo record);//添加了商品详情

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);//修改库存，感觉有点问题
    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);//批量修改商品状态
    // 查
    GoodsInfo selectByPrimaryKey(Long goodsId);
    GoodsInfo selectByCategoryIdAndName(@Param("goodsName") String goodsName, @Param("goodsCategoryId") Long goodsCategoryId);

    List<GoodsInfo> findGoodsList(PageQueryUtil pageUtil);//按条件获取商品列表
    int getTotalGoodsCount(PageQueryUtil pageUtil);//按条件获取商品数量

    List<GoodsInfo> selectByPrimaryKeys(List<Long> goodsIds);

    List<GoodsInfo> findGoodsListBySearch(PageQueryUtil pageUtil);//搜索商品
    int getTotalGoodsBySearch(PageQueryUtil pageUtil);
























}
