package com.dhu.ecommercesystem.service.impl;

import com.dhu.ecommercesystem.common.ServiceResultEnum;
import com.dhu.ecommercesystem.entity.Carousel;
import com.dhu.ecommercesystem.entity.vo.IndexCarouselVO;
import com.dhu.ecommercesystem.mapper.CarouselMapper;
import com.dhu.ecommercesystem.service.CarouselService;
import com.dhu.ecommercesystem.util.BeanUtil;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/20-19:42
 */
@Service
public class CarouselServiceImpl implements CarouselService {

    @Resource
    CarouselMapper carouselMapper;

    @Override
    public String saveCarousel(Carousel carousel) {
        int i = carouselMapper.insertSelective(carousel);
        if(i>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        int i = carouselMapper.deleteBatch(ids);
        if(i>0){
            return true;
        }
        return false;
    }

    @Override
    public String updateCarousel(Carousel carousel) {
        Carousel temp = carouselMapper.selectByPrimaryKey(carousel.getCarouselId());
        if(temp==null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        temp.setCarouselUrl(carousel.getCarouselUrl());
        temp.setCarouselRank(carousel.getCarouselRank());
        temp.setRedirectUrl(carousel.getRedirectUrl());
        temp.setUpdateTime(new Date());
        int i = carouselMapper.updateSelective(temp);
        if(i>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Carousel getCarouselById(Integer id) {
        Carousel carousel = carouselMapper.selectByPrimaryKey(id);
        if(carousel==null){
            return null;
        }
        return carousel;
    }

    @Override
    public PageResult getCarouselPage(PageQueryUtil pageQueryUtil) {
        List<Carousel> carouselList = carouselMapper.findCarouselList(pageQueryUtil);
        int total = carouselMapper.getTotalCarousels(pageQueryUtil);
        PageResult pageResult = new PageResult(carouselList,total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    public List<IndexCarouselVO> getCarouseForIndex(int number) {
        List<IndexCarouselVO> indexCarouselVOS = new ArrayList<>();
        List<Carousel> carousels = carouselMapper.findCarouselsByNum(number);
        if(!CollectionUtils.isEmpty(carousels)){
            indexCarouselVOS = BeanUtil.copyList(carousels,IndexCarouselVO.class);
        }
        return indexCarouselVOS;
    }
}
