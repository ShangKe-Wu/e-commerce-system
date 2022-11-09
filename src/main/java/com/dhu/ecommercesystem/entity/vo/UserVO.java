package com.dhu.ecommercesystem.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:WuShangke
 * @create:2022/7/19-21:04
 * 用户VO
 */
@Data
public class UserVO implements Serializable {
    private Long userId;

    private String nickName;

    private String loginName;

    private String introduceSign;

    private String address;

    private int shopCartItemCount;
}
