package com.dhu.ecommercesystem.mapper;

import com.dhu.ecommercesystem.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author:WuShangke
 * @create:2022/7/15-20:50
 */
@Mapper
public interface AdminUserMapper {
    // 增
    int insert(AdminUser record);
    int insertSelective(AdminUser record);
    // 改
    int updateByPrimaryKeySelective(AdminUser record);
    int updateByPrimaryKey(AdminUser record);
    // 查
    AdminUser login(@Param("userName") String userName, @Param("password") String password);//登录
    AdminUser selectByPrimaryKey(@Param("id") Integer id);
}
