package com.dhu.ecommercesystem.service.impl;

import com.dhu.ecommercesystem.common.Constants;
import com.dhu.ecommercesystem.common.NewBeeMallException;
import com.dhu.ecommercesystem.common.ServiceResultEnum;
import com.dhu.ecommercesystem.entity.MallUser;
import com.dhu.ecommercesystem.entity.vo.UserVO;
import com.dhu.ecommercesystem.mapper.MallUserMapper;
import com.dhu.ecommercesystem.mapper.ShoppingCartItemMapper;
import com.dhu.ecommercesystem.service.UserService;
import com.dhu.ecommercesystem.util.*;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/22-21:51
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    MallUserMapper userMapper;
    @Resource
    ShoppingCartItemMapper shoppingCartItemMapper;
    @Override
    public String register(String userName, String password) {
        MallUser temp = userMapper.selectByLoginName(userName);
        if(temp!=null){
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser mallUser = new MallUser();
        mallUser.setLoginName(userName);
        mallUser.setNickName(userName);
        String passwordMD5 = MD5Util.MD5Encode(password,"UTF-8");
        mallUser.setPasswordMd5(passwordMD5);
        if(userMapper.insertSelective(mallUser)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String login(String userName, String passwordMD5, HttpSession httpSession) {
        MallUser mallUser = userMapper.selectByLoginNameAndPasswd(userName, passwordMD5);
        if(mallUser!=null && httpSession!=null){
            //判断用户是否被封
            if(mallUser.getLockedFlag()==1){
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            //昵称太长则修改昵称
            if(mallUser.getNickName()!=null && mallUser.getNickName().length()>7){
                mallUser.setNickName(mallUser.getNickName().substring(0,7)+"..");
            }
            UserVO userVO = new UserVO();
            BeanUtil.copyProperties(mallUser,userVO);
            //封装到session中，设置购物车的商品的数量
            int count = shoppingCartItemMapper.selectCountByUserId(mallUser.getUserId());
            userVO.setShopCartItemCount(count);
            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY,userVO);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public PageResult getUserPage(PageQueryUtil pageQueryUtil) {
        List<MallUser> mallUserList = userMapper.findMallUserList(pageQueryUtil);
        int total = userMapper.getTotalMallUsers(pageQueryUtil);
        PageResult pageResult = new PageResult(mallUserList,total,pageQueryUtil.getLimit(),pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    public UserVO updateUserInfo(MallUser mallUser, HttpSession httpSession) {
        //更改用户信息是更改session里面的
        UserVO userVO=(UserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallUser userFromDB = userMapper.selectByPrimaryKey(userVO.getUserId());
        if(userFromDB!=null){
            if (!StringUtils.isEmpty(mallUser.getNickName())) {
                userFromDB.setNickName(NewBeeMallUtil.cleanString(mallUser.getNickName()));
            }
            if (!StringUtils.isEmpty(mallUser.getAddress())) {
                userFromDB.setAddress(NewBeeMallUtil.cleanString(mallUser.getAddress()));
            }
            if (!StringUtils.isEmpty(mallUser.getIntroduceSign())) {
                userFromDB.setIntroduceSign(NewBeeMallUtil.cleanString(mallUser.getIntroduceSign()));
            }
            if (userMapper.updateByPrimaryKeySelective(userFromDB) > 0) {
                UserVO MallUserVO = new UserVO();
                userFromDB = userMapper.selectByPrimaryKey(mallUser.getUserId());
                BeanUtil.copyProperties(userFromDB, MallUserVO);
                MallUserVO.setShopCartItemCount(userVO.getShopCartItemCount());
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, MallUserVO);
                return MallUserVO;
            }
        }
        NewBeeMallException.fail("用户不存在");
        return null;
    }

    @Override
    public Boolean lockUser(Long[] ids, int lockStatus) {
        if(ids.length<1){
            return false;
        }
        return userMapper.lockUserBatch(ids,lockStatus)>0;
    }
}
