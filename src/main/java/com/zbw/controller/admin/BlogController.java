package com.zbw.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zbw.constants.DeleteStatusEnum;
import com.zbw.constants.HttpStatusEnum;
import com.zbw.entity.Blog;
import com.zbw.entity.BlogCategory;
import com.zbw.entity.BlogTagRelation;
import com.zbw.entity.BlogTags;
import com.zbw.pojo.dto.AjaxPutPage;
import com.zbw.pojo.dto.AjaxResultPage;
import com.zbw.pojo.dto.BlogPageCondition;
import com.zbw.pojo.dto.Result;
import com.zbw.pojo.vo.BlogVo;
import com.zbw.service.BlogService;
import com.zbw.service.BlogTagRelationService;
import com.zbw.service.CategoryService;
import com.zbw.service.TagService;
import com.zbw.util.DateUtils;
import com.zbw.util.PageQueryUtil;
import com.zbw.util.ResultGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogTagRelationService blogTagRelationService;

    /**
     * 博客（分页）列表  有参数，用于翻页
     * @param ajaxPutPage
     * @param condition
     * @return
     */
    @GetMapping("/blog/list")
    @ResponseBody
    public AjaxResultPage<BlogVo> list(AjaxPutPage<Blog> ajaxPutPage, Blog condition){

        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>(condition);
        queryWrapper.orderByDesc(Blog::getPriority);
        queryWrapper.orderByDesc(Blog::getUpdateTime);

        Page<Blog> page = ajaxPutPage.putPageToPage();//分页信息存放在这
        blogService.page(page, queryWrapper);

        List<BlogVo> blogList = blogService.findAndPage(page);

        AjaxResultPage<BlogVo> result = new AjaxResultPage<>();
        result.setData(blogList);
        result.setCount(page.getTotal());
        return result;
    }

    @GetMapping("/blog")
    public String list(){
        return "admin/blog-list";
    }

    /**
     * 编辑博客
     * @param model
     * @param blogId
     * @return
     */
    @GetMapping("/blog/edit")
    public String edit(Model model,@RequestParam(required = false) Long blogId){
        if (blogId != null) {
            Blog blog = blogService.getById(blogId);
            List<BlogTagRelation> list = blogTagRelationService.list(
                    new LambdaQueryWrapper<BlogTagRelation>()
                            .eq(BlogTagRelation::getBlogId, blogId)
            );
            List<Integer> tags = null;
            if (!CollectionUtils.isEmpty(list)) {
                tags = list.stream().map(
                        BlogTagRelation::getTagId)
                        .collect(Collectors.toList());
            }
            model.addAttribute("blogTags", tags);
            model.addAttribute("blogInfo", blog);
        }
        return "admin/blog-edit";
    }

    /**
     * 保存blog
     * @param blogTagIds
     * @param blogInfo
     * @return
     */
    @ResponseBody
    @PostMapping("/blog/edit")
    public Result<String> saveBlog(@RequestParam("blogTagIds[]") List<Integer> blogTagIds, Blog blogInfo) throws Exception{
        if (CollectionUtils.isEmpty(blogTagIds) || ObjectUtils.isEmpty(blogInfo)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.BAD_REQUEST);
        }

        //验证blogId是否存在，不存在则是save
        if(blogService.count(
                new LambdaQueryWrapper<Blog>()
                        .eq(Blog::getBlogId,blogInfo.getBlogId())
        ) <= 0){
            //先验证一下博客标题是否唯一
            int count = blogService.count(new LambdaQueryWrapper<Blog>()
                    .eq(Blog::getBlogTitle, blogInfo.getBlogTitle()));
            if(count > 0){
                return ResultGenerator.getFailResult(HttpStatusEnum.BAD_REQUEST.getStatus(),"该标题已存在");
            }
        }
        blogInfo.setPriority(blogInfo.getPriority() == null ? 0 : blogInfo.getPriority());
        blogInfo.setCreateTime(DateUtils.getLocalCurrentDate());
        blogInfo.setUpdateTime(DateUtils.getLocalCurrentDate());

        //如果是编辑的话，需要更新缓存
        blogService.updateCache(blogInfo);

        //mybatis-plus的保存或更新方法
        if (blogService.saveOrUpdate(blogInfo)) {
            blogTagRelationService.removeAndsaveBatch(blogTagIds, blogInfo);
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 更新缓存  手动
     * @return
     */
    @ResponseBody
    @GetMapping("/blog/clearCache")
    public boolean clearCache() throws Exception{

        blogService.setBlogCache(new BlogPageCondition().setPageNum(1).setPageSize(10));
        return true;
    }

    /**
     * 删除blog，更新状态为0
     * blog.delete_status更新为0
     * @param blogId
     * @return
     */
    @ResponseBody
    @PostMapping("/blog/delete")
    public Result deleteBlog(@RequestParam Long blogId) {
        Blog blogInfo = new Blog()
                .setBlogId(blogId)
                .setDeleteStatus(DeleteStatusEnum.DELETED.getStatus())
                .setUpdateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogService.updateById(blogInfo);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 还原，更新状态为1
     * @param blogId
     * @return
     */
    @ResponseBody
    @PostMapping("/blog/restore")
    public Result restoreBlog(@RequestParam Long blogId) {
        Blog blogInfo = new Blog()
                .setBlogId(blogId)
                .setDeleteStatus(DeleteStatusEnum.NO_DELETED.getStatus())
                .setUpdateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogService.updateById(blogInfo);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 清除blog，删除数据库数据
     * @param blogId
     * @return
     */
    @ResponseBody
    @PostMapping("/blog/clear")
    public Result<String> clearBlog(@RequestParam Long blogId) {
        if (blogService.clearBlogInfo(blogId)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }


    @ResponseBody
    @GetMapping("/blog/select")
    public List<Blog> getBlogSelect() {
        return blogService.list();
    }

    /**
     * 更新博客状态，发布、评论允许状态,置顶状态
     * @param blog
     * @return
     */
    @PostMapping("/blog/blogStatus")
    @ResponseBody
    public Result<String> updateBlogStatus(Blog blog){

        blog.setUpdateTime(DateUtils.getLocalCurrentDate());
        if(blogService.updateById(blog)){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }
}
