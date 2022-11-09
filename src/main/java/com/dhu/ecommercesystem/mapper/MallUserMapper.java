package com.dhu.ecommercesystem.mapper;

import com.dhu.ecommercesystem.entity.MallUser;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/17-21:33
 */
@Mapper
public interface MallUserMapper {
    // 增
    int insert(MallUser record);
    int insertSelective(MallUser record);
    // 删
    int deleteByPrimaryKey(Long userId);
    // 改
    int updateByPrimaryKeySelective(MallUser record);
    int updateByPrimaryKey(MallUser record);
    int lockUserBatch(@Param("ids") Long[] ids, @Param("lockStatus") int lockStatus);
    // 查
    MallUser selectByPrimaryKey(Long userId);
    MallUser selectByLoginName(String loginName);
    MallUser selectByLoginNameAndPasswd(@Param("loginName") String loginName, @Param("password") String password);

    List<MallUser> findMallUserList(PageQueryUtil pageUtil);
    int getTotalMallUsers(PageQueryUtil pageUtil);
}
