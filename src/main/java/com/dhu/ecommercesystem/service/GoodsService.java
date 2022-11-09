package com.dhu.ecommercesystem.service;

import com.dhu.ecommercesystem.entity.GoodsInfo;
import com.dhu.ecommercesystem.entity.vo.GoodsDetailsVO;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;

import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/20-22:20
 */
public interface GoodsService {
    // 增
    String saveGoods(GoodsInfo goodsInfo);
    //批量增加商品
    void batchSaveGoods(List<GoodsInfo> goodsInfoList);
    // 删
    // (调整商品上架下架状态)
    Boolean batchUpdateSellStatus(Long[] goodsId,int sellStatus);
    // 改
    String updateGoodsInfo(GoodsInfo goodsInfo);
    // 查
    GoodsInfo getGoodsById(Long goodsId);
    GoodsDetailsVO getGoodsDetailById(Long goodsId);
    // 后台分页
    PageResult getGoodsPage(PageQueryUtil pageQueryUtil);
    // 商品搜索页面
    PageResult getSearchGoodsPage(PageQueryUtil pageQueryUtil);
}
