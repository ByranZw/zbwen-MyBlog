package com.zbw.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zbw.config.RedisConfig;
import com.zbw.constants.HttpStatusEnum;
import com.zbw.entity.BlogCategory;
import com.zbw.pojo.dto.AjaxPutPage;
import com.zbw.pojo.dto.AjaxResultPage;
import com.zbw.pojo.dto.BlogPageCondition;
import com.zbw.pojo.dto.Result;
import com.zbw.service.CategoryService;
import com.zbw.util.DateUtils;
import com.zbw.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/admin")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/category")
    public String gotoList() {
        return "admin/category-list";
    }


    @GetMapping("/category/list")
    @ResponseBody
    public Result<List<BlogCategory>> list() throws Exception{

        List<BlogCategory> list = categoryService.getCategoryList();
        if (CollectionUtils.isEmpty(list)) {
            ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, list);
    }

    /**
     * 分页获取category列表
     *
     * @param ajaxPutPage
     * @param condition
     * @return
     */
    @ResponseBody
    @GetMapping("/category/paging")
    public AjaxResultPage<BlogCategory> getCategoryList(AjaxPutPage<BlogCategory> ajaxPutPage, BlogCategory condition) {

        QueryWrapper<BlogCategory> queryWrapper = new QueryWrapper<>(condition);
        queryWrapper.lambda()
                .orderByAsc(BlogCategory::getCategoryRank);
        Page<BlogCategory> page = ajaxPutPage.putPageToPage();
        categoryService.page(page, queryWrapper);


        AjaxResultPage<BlogCategory> result = new AjaxResultPage<>();
        result.setData(page.getRecords());
        result.setCount(page.getTotal());
        return result;
    }

    /**
     * 修改分类信息
     *
     * @param blogCategory
     * @return
     */
    @ResponseBody
    @PostMapping("/category/update")
    public Result<String> updateCategory(BlogCategory blogCategory) {

        if (categoryService.updateById(blogCategory))
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        return ResultGenerator.getResultByHttp(HttpStatusEnum.BAD_REQUEST);
    }

    /**
     * 添加分类目录
     *
     * @param blogCategory
     * @return
     */
    @ResponseBody
    @PostMapping("/category/add")
    public Result<String> addCategory(BlogCategory blogCategory) {
        blogCategory.setCreateTime(DateUtils.getLocalCurrentDate());
        boolean flag = categoryService.save(blogCategory);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 弹出添加框
     *
     * @return
     */
    @GetMapping("/category/add")
    public String addBlogConfig() {
        return "admin/category-add";
    }

    /**
     * 修改分类信息【状态变更】
     *
     * @param blogCategory
     * @return
     */
    @ResponseBody
    @PostMapping("/category/deleteStatus")
    public Result<String> updateCategoryStatus(BlogCategory blogCategory) {
        if (categoryService.updateById(blogCategory)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    @ResponseBody
    @PostMapping("/category/clear")
    public Result<String> clearCategory(BlogCategory blogCategory) {
        if (categoryService.clearCategory(blogCategory)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 更新缓存  手动
     * @return
     */
    @ResponseBody
    @GetMapping("/category/clearCache")
    public boolean clearCache() throws Exception{

        categoryService.setCategoryCache();
        return true;
    }
}
