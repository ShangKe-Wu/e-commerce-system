package com.dhu.ecommercesystem.service;

import com.dhu.ecommercesystem.entity.IndexConfig;
import com.dhu.ecommercesystem.entity.vo.IndexConfigGoodsVO;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;

import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/21-11:28
 */
public interface IndexConfigService {
    // 增
    String saveIndexConfig(IndexConfig indexConfig);
    // 删
    Boolean batchDelete(Long ids[]);
    // 改
    String updateIndexConfig(IndexConfig indexConfig);
    // 查
    IndexConfig getIndexConfigById(Long Id);
    // 后台分页
    PageResult getConfigPage(PageQueryUtil pageQueryUtil);
    // 返回固定数量的首页配置商品对象（首页调用）
    List<IndexConfigGoodsVO> getConfigGoodsForIndex(int configType,int number);
}
