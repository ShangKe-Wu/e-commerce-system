package com.dhu.ecommercesystem.entity.vo;

import com.dhu.ecommercesystem.entity.GoodsCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/19-21:05
 * 搜索页面种类VO
 */
@Data
public class SearchPageCategoryVO implements Serializable {
    private String firstLevelCategoryName;

    private List<GoodsCategory> secondLevelCategoryList;

    private String secondLevelCategoryName;

    private List<GoodsCategory> thirdLevelCategoryList;

    private String currentCategoryName;
}
