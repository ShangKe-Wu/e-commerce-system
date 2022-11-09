package com.dhu.ecommercesystem.controller.admin;

import com.dhu.ecommercesystem.common.IndexConfigTypeEnum;
import com.dhu.ecommercesystem.common.NewBeeMallException;
import com.dhu.ecommercesystem.common.ServiceResultEnum;
import com.dhu.ecommercesystem.entity.IndexConfig;
import com.dhu.ecommercesystem.service.IndexConfigService;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.Result;
import com.dhu.ecommercesystem.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * @author:WuShangke
 * @create:2022/7/25-21:04
 */
@Controller
@RequestMapping("/admin")
public class AdminGoodsIndexConfigController {
    @Resource
    IndexConfigService indexConfigService;
    //页面跳转
    @GetMapping("/indexConfigs")
    public String indexPage(HttpServletRequest request, @RequestParam ("configType") int configType){
        IndexConfigTypeEnum indexConfigTypeEnumByType = IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType);
        if(indexConfigTypeEnumByType.equals(IndexConfigTypeEnum.DEFAULT)){
            NewBeeMallException.fail("参数异常");
        }
        request.setAttribute("path",indexConfigTypeEnumByType.getName());
        request.setAttribute("configType",configType);
        return "admin/newbee_mall_index_config";
    }
    // 增
    @PostMapping("/indexConfigs/save")
    @ResponseBody
    public Result save(@RequestBody IndexConfig indexConfig){
        if (Objects.isNull(indexConfig.getConfigType())
                || StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = indexConfigService.saveIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }
    // 删
    @PostMapping("/indexConfigs/delete")
    @ResponseBody
    public Result delete(@RequestBody Long ids[]){
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (indexConfigService.batchDelete(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }
    // 改
    @PostMapping("/indexConfigs/update")
    @ResponseBody
    public Result update(@RequestBody IndexConfig indexConfig){
        if (Objects.isNull(indexConfig.getConfigType())
                || Objects.isNull(indexConfig.getConfigId())
                || StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = indexConfigService.updateIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }
    // 查
    @GetMapping("/indexConfigs/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id){
        IndexConfig config = indexConfigService.getIndexConfigById(id);
        if (config == null) {
            return ResultGenerator.genFailResult("未查询到数据");
        }
        return ResultGenerator.genSuccessResult(config);
    }
    //列表
    @GetMapping("/indexConfigs/list")
    @ResponseBody
    public Result list(@RequestParam Map<String,Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(indexConfigService.getConfigPage(pageUtil));
    }
}
