package com.dhu.ecommercesystem.service.impl;

import com.dhu.ecommercesystem.common.CategoryLevelEnum;
import com.dhu.ecommercesystem.common.Constants;
import com.dhu.ecommercesystem.common.NewBeeMallException;
import com.dhu.ecommercesystem.common.ServiceResultEnum;
import com.dhu.ecommercesystem.entity.GoodsCategory;
import com.dhu.ecommercesystem.entity.GoodsInfo;
import com.dhu.ecommercesystem.entity.vo.GoodsDetailsVO;
import com.dhu.ecommercesystem.entity.vo.SearchGoodsVO;
import com.dhu.ecommercesystem.mapper.GoodsCategoryMapper;
import com.dhu.ecommercesystem.mapper.GoodsInfoMapper;
import com.dhu.ecommercesystem.service.GoodsService;
import com.dhu.ecommercesystem.util.BeanUtil;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/21-10:46
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    @Resource
    GoodsInfoMapper goodsInfoMapper;
    @Resource
    GoodsCategoryMapper categoryMapper;

    @Override
    public String saveGoods(GoodsInfo goodsInfo) {
        //商品的分类不存在，或者不是三级标签也失败
        GoodsCategory goodsCategory = categoryMapper.selectByPrimaryKey(goodsInfo.getGoodsCategoryId());
        if(goodsCategory==null || goodsCategory.getCategoryLevel() != CategoryLevelEnum.LEVEL_THREE.getLevel()){
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        //商品存在则插入失败
        GoodsInfo temp1 = goodsInfoMapper.selectByCategoryIdAndName(goodsInfo.getGoodsName(),goodsInfo.getGoodsId());
        if(temp1!=null){
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        goodsInfo.setCreateTime(new Date());
        goodsInfo.setUpdateTime(new Date());
        int insert = goodsInfoMapper.insertSelective(goodsInfo);
        if(insert>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveGoods(List<GoodsInfo> goodsInfoList) {
        if(!CollectionUtils.isEmpty(goodsInfoList)){
            goodsInfoMapper.batchInsert(goodsInfoList);
        }
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] goodsId, int sellStatus) {
        if(goodsInfoMapper.batchUpdateSellStatus(goodsId,sellStatus)>0){
            return true;
        }
        return false;
    }

    @Override
    public String updateGoodsInfo(GoodsInfo goodsInfo) {
        //不是三级标签，或者标签不存在，则更新异常
        GoodsCategory goodsCategory = categoryMapper.selectByPrimaryKey(goodsInfo.getGoodsCategoryId());
        if(goodsCategory==null || goodsCategory.getCategoryLevel()!=CategoryLevelEnum.LEVEL_THREE.getLevel()){
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        //商品不存在则更新异常
        GoodsInfo goodsInfo1 = goodsInfoMapper.selectByPrimaryKey(goodsInfo.getGoodsId());
        if(goodsInfo1==null){
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        //商品中已经存在一个名称和标签相同的商品（GoodsId不同），返回异常
        GoodsInfo goodsInfo2 = goodsInfoMapper.selectByCategoryIdAndName(goodsInfo.getGoodsName(), goodsInfo.getGoodsCategoryId());
        if(goodsInfo2!=null && !goodsInfo2.getGoodsId().equals(goodsInfo.getGoodsId())){
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        //进行更新
        goodsInfo.setUpdateTime(new Date());
        int result = goodsInfoMapper.updateByPrimaryKeySelective(goodsInfo);
        if(result>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public GoodsInfo getGoodsById(Long goodsId) {
        GoodsInfo goodsInfo = goodsInfoMapper.selectByPrimaryKey(goodsId);
        if(goodsInfo==null){
            ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        return goodsInfo;
    }

    @Override
    public GoodsDetailsVO getGoodsDetailById(Long goodsId) {
        if(goodsId<1){
            NewBeeMallException.fail("参数异常");
        }
        GoodsInfo goodsInfo = goodsInfoMapper.selectByPrimaryKey(goodsId);
        //检查商品是否存在
        if(goodsInfo==null){
            NewBeeMallException.fail("商品不存在");
        }
        //检查是否已下架
        if(goodsInfo.getGoodsSellStatus()!= Constants.SELL_STATUS_UP){
            NewBeeMallException.fail("商品已下架");
        }
        GoodsDetailsVO vo =new GoodsDetailsVO();
        BeanUtil.copyProperties(goodsInfo,vo);
        vo.setGoodsCarouselList(goodsInfo.getGoodsCarousel().split(","));
        return vo;
    }

    @Override
    public PageResult getGoodsPage(PageQueryUtil pageQueryUtil) {
        List<GoodsInfo> goodsList = goodsInfoMapper.findGoodsList(pageQueryUtil);
        int totalGoodsCount = goodsInfoMapper.getTotalGoodsCount(pageQueryUtil);
        PageResult pageResult = new PageResult(goodsList,totalGoodsCount,pageQueryUtil.getLimit(),pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    public PageResult getSearchGoodsPage(PageQueryUtil pageQueryUtil) {
        List<GoodsInfo> goodsListBySearch = goodsInfoMapper.findGoodsListBySearch(pageQueryUtil);
        int totalGoodsBySearch = goodsInfoMapper.getTotalGoodsBySearch(pageQueryUtil);
        List<SearchGoodsVO> searchGoodsVOS = new ArrayList<>();
        if(!CollectionUtils.isEmpty(goodsListBySearch)){
            searchGoodsVOS = BeanUtil.copyList(goodsListBySearch,SearchGoodsVO.class);
            for(SearchGoodsVO vo :searchGoodsVOS){
                String goodsName = vo.getGoodsName();
                String goodsIntro = vo.getGoodsIntro();
                //解决字符串太长的问题
                if(goodsName.length()>28){
                    goodsName = goodsName.substring(0,28)+"...";
                    vo.setGoodsName(goodsName);
                }
                if(goodsIntro.length()>30){
                    goodsIntro = goodsIntro.substring(0,30)+"...";
                    vo.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(searchGoodsVOS,totalGoodsBySearch,pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }
}
