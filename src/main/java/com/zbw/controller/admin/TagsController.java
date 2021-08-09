package com.zbw.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zbw.constants.DeleteStatusEnum;
import com.zbw.constants.HttpStatusEnum;
import com.zbw.entity.BlogTags;
import com.zbw.pojo.dto.AjaxPutPage;
import com.zbw.pojo.dto.AjaxResultPage;
import com.zbw.pojo.dto.Result;
import com.zbw.service.BlogService;
import com.zbw.service.TagService;
import com.zbw.util.DateUtils;
import com.zbw.util.PageQueryUtil;

import com.zbw.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class TagsController {


    @Autowired
    private TagService tagService;

    @Autowired
    private BlogService blogService;


    @GetMapping("/tags")
    public String tagPage() {
        return "admin/tag-list";
    }


    /**
     * 获取未删除状态下的所有标签
     * @return
     */
    @GetMapping("/tags/list")
    @ResponseBody
    public Result<List<BlogTags>> list() {

        List<BlogTags> list = tagService.list(
                new LambdaQueryWrapper<BlogTags>()
                        .eq(BlogTags::getDeleteStatus, DeleteStatusEnum.NO_DELETED.getStatus()));

        if (CollectionUtils.isEmpty(list)) {
            ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, list);
    }

    /**
     * 分页获取标签
     * @param ajaxPutPage
     * @param condition
     * @return
     */
    @ResponseBody
    @GetMapping("/tags/paging")
    public AjaxResultPage<BlogTags> getCategoryList(AjaxPutPage<BlogTags> ajaxPutPage, BlogTags condition){
        LambdaQueryWrapper<BlogTags> queryWrapper = new LambdaQueryWrapper<>(condition);
        //排除tagId = 1,默认标签
        queryWrapper.ne(BlogTags::getTagId,1);
        Page<BlogTags> page = ajaxPutPage.putPageToPage();
        tagService.page(page,queryWrapper);
        AjaxResultPage<BlogTags> result = new AjaxResultPage<>();
        result.setData(page.getRecords());
        result.setCount(page.getTotal());
        return result;
    }

    /**
     * 添加标签
     * @param blogTag
     * @return
     */
    @ResponseBody
    @PostMapping("/tags/add")
    public Result<String> addTag(BlogTags blogTag){
        blogTag.setCreateTime(DateUtils.getLocalCurrentDate());
        int count = tagService.count(new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getTagName, blogTag.getTagName()));
        if(count > 0){
            return ResultGenerator.getFailResult(400,"标签名已存在！！");
        }
        boolean flag = tagService.save(blogTag);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }else {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 修改标签名字
     * @param blogTag
     * @return
     */
    @ResponseBody
    @PostMapping("/tags/update")
    public Result<String> updateCategory(BlogTags blogTag) {

        BlogTags sqlBlogTag = tagService.getById(blogTag.getTagId());
        boolean flag = sqlBlogTag.getTagName().equals(blogTag.getTagName());
        if (!flag) {
            tagService.updateById(blogTag);

        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
    }

    /**
     * 修改标签状态
     * @param blogTag
     * @return
     */
    @ResponseBody
    @PostMapping("/tags/delete_status")
    public Result<String> updateCategoryStatus(BlogTags blogTag){
        boolean flag = tagService.updateById(blogTag);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 清除标签
     * @param tagId
     * @return
     * @throws RuntimeException
     */
    @ResponseBody
    @PostMapping("/tags/clear")
    public Result<String> clearTag(Integer tagId) throws RuntimeException{
        if (tagService.clearTag(tagId)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

}