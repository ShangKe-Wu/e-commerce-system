package com.dhu.ecommercesystem.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author:WuShangke
 * @create:2022/7/15-20:04
 */
@Data
@ToString
public class AdminUser {
    private Integer adminUserId;
    private String loginUserName;
    private String loginPassword;
    private String nickName;
    private Byte locked;
}
