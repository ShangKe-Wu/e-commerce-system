package com.dhu.ecommercesystem.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/19-21:07
 * 首页分类数据（二级）
 */
@Data
public class SecondLevelCategoryVO implements Serializable {
    private Long categoryId;

    private Long parentId;

    private Byte categoryLevel;

    private String categoryName;

    private List<ThirdLevelCategoryVO> thirdLevelCategoryVOS;
}
