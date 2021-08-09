package com.zbw.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zbw.constants.HttpStatusEnum;
import com.zbw.entity.Config;
import com.zbw.pojo.dto.AjaxResultPage;
import com.zbw.pojo.dto.BlogPageCondition;
import com.zbw.pojo.dto.Result;
import com.zbw.service.ConfigService;

import com.zbw.util.DateUtils;
import com.zbw.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class ConfigurationsController {

    @Autowired
    private ConfigService configService;

    @GetMapping("/blogConfig")
    public String gotoBlogConfig(){
        return "admin/sys-edit";
    }

    /**
     * 获取config列表
     * @return
     */
    @ResponseBody
    @GetMapping("/blogConfig/list")
    public AjaxResultPage<Config> getBlogConfig() throws Exception{
        AjaxResultPage<Config> ajaxResultPage = new AjaxResultPage<>();
        List<Config> list = null;
            list = configService.getAllConfigsList();

        if (CollectionUtils.isEmpty(list)){
            ajaxResultPage.setCode(500);
            return ajaxResultPage;
        }
        ajaxResultPage.setData(configService.list());
        return ajaxResultPage;
    }

    /**
     * 修改系统配置
     * @param blogConfig
     * @return
     */
    @ResponseBody
    @PostMapping("/blogConfig/edit")
    public Result<String> updateBlogConfig(Config blogConfig){
        blogConfig.setUpdateTime(DateUtils.getLocalCurrentDate());
        boolean flag = configService.updateById(blogConfig);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }else{
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 添加系统配置
     * @return
     */
    @GetMapping("/blogConfig/add")
    public String addBlogConfig(){
        return "admin/sys-add";
    }

    /**
     * 新增系统信息项
     */
    @ResponseBody
    @PostMapping("/blogConfig/add")
    public Result<String> addBlogConfig(Config blogConfig){
        blogConfig.setCreateTime(DateUtils.getLocalCurrentDate());
        blogConfig.setUpdateTime(DateUtils.getLocalCurrentDate());
        boolean flag = configService.save(blogConfig);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }else{
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除配置信息项
     */
    @ResponseBody
    @PostMapping("/blogConfig/del")
    public Result<String> delBlogConfig(@RequestParam String configField){
        boolean flag = configService.removeById(configField);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }else{
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新缓存  手动
     * @return
     */
    @ResponseBody
    @GetMapping("/blogConfig/clearCache")
    public boolean clearCache() throws Exception{

        configService.setConfigCache();
        return true;
    }


}
