package com.dhu.ecommercesystem.service.impl;

import com.dhu.ecommercesystem.common.ServiceResultEnum;
import com.dhu.ecommercesystem.entity.GoodsInfo;
import com.dhu.ecommercesystem.entity.IndexConfig;
import com.dhu.ecommercesystem.entity.vo.IndexConfigGoodsVO;
import com.dhu.ecommercesystem.mapper.GoodsInfoMapper;
import com.dhu.ecommercesystem.mapper.IndexConfigMapper;
import com.dhu.ecommercesystem.service.IndexConfigService;
import com.dhu.ecommercesystem.util.BeanUtil;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;
import com.sun.xml.internal.txw2.output.IndentingXMLFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author:WuShangke
 * @create:2022/7/21-11:42
 */
@Service
public class IndexConfigServiceImpl implements IndexConfigService {
    @Resource
    IndexConfigMapper indexConfigMapper;
    @Resource
    GoodsInfoMapper goodsInfoMapper;

    @Override
    public String saveIndexConfig(IndexConfig indexConfig) {
        //判断商品是否存在
        if(goodsInfoMapper.selectByPrimaryKey(indexConfig.getGoodsId())==null){
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        //判断是否存在同类型同个商品id的 indexConfig
        if(indexConfigMapper.selectByTypeAndGoodsId(indexConfig.getConfigType(), indexConfig.getGoodsId())!=null){
            return ServiceResultEnum.SAME_INDEX_CONFIG_EXIST.getResult();
        }
        if(indexConfigMapper.insertSelective(indexConfig)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Boolean batchDelete(Long[] ids) {
        if(indexConfigMapper.deleteBatch(ids)>0){
            return true;
        }
        return false;
    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        //商品不存在则无法更新
        if(goodsInfoMapper.selectByPrimaryKey(indexConfig.getGoodsId())==null){
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        //首页配置不存在也无法更新
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if(temp==null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //首页配置相同也无法更新
        IndexConfig temp2 = indexConfigMapper.selectByTypeAndGoodsId(indexConfig.getConfigType(), indexConfig.getGoodsId());
        if(temp2!=null && !temp2.getConfigId().equals(indexConfig.getConfigId())){
            return ServiceResultEnum.SAME_INDEX_CONFIG_EXIST.getResult();
        }
        indexConfig.setUpdateTime(new Date());
        if(indexConfigMapper.updateByPrimaryKeySelective(indexConfig)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public IndexConfig getIndexConfigById(Long Id) {
        return indexConfigMapper.selectByPrimaryKey(Id);
    }

    @Override
    public PageResult getConfigPage(PageQueryUtil pageQueryUtil) {
        List<IndexConfig> indexConfigList = indexConfigMapper.findIndexConfigList(pageQueryUtil);
        int totalIndexConfigs = indexConfigMapper.getTotalIndexConfigs(pageQueryUtil);
        PageResult pageResult = new PageResult(indexConfigList,totalIndexConfigs,pageQueryUtil.getLimit(),pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    public List<IndexConfigGoodsVO> getConfigGoodsForIndex(int configType, int number) {
        List<IndexConfig> indexConfigsByTypeAndNum = indexConfigMapper.findIndexConfigsByTypeAndNum(configType, number);
        List<IndexConfigGoodsVO> goodsVOList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(indexConfigsByTypeAndNum)){
            //取出所有的goodsId
            List<Long> goodsIds = indexConfigsByTypeAndNum.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<GoodsInfo> goodsInfos = goodsInfoMapper.selectByPrimaryKeys(goodsIds);
            //拷贝属性
            goodsVOList=BeanUtil.copyList(goodsInfos,IndexConfigGoodsVO.class);
            //处理过长的字符串
            for(IndexConfigGoodsVO vo:goodsVOList){
                String goodsName = vo.getGoodsName();
                String goodsIntro = vo.getGoodsIntro();
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
        return goodsVOList;
    }
}
