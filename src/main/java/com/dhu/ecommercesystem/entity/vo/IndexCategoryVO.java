package com.dhu.ecommercesystem.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/19-20:52
 * 首页分类数据VO
 */
@Data
public class IndexCategoryVO implements Serializable {
    private Long categoryId;

    private Byte categoryLevel;

    private String categoryName;

    private List<SecondLevelCategoryVO> secondLevelCategoryVOS;
}
