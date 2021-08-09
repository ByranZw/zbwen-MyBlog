package com.zbw.controller.blog;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zbw.constants.BlogStatusEnum;
import com.zbw.constants.CommentStatusEnum;
import com.zbw.constants.DeleteStatusEnum;
import com.zbw.constants.HttpStatusEnum;
import com.zbw.controller.admin.LinkController;
import com.zbw.entity.*;
import com.zbw.pojo.dto.AjaxPutPage;
import com.zbw.pojo.dto.AjaxResultPage;
import com.zbw.pojo.dto.BlogPageCondition;
import com.zbw.pojo.dto.Result;
import com.zbw.pojo.vo.BlogDetailVO;
import com.zbw.pojo.vo.BlogVo;
import com.zbw.service.*;
import com.zbw.util.DateUtils;
import com.zbw.util.PageResult;
import com.zbw.util.RequestUtil;
import com.zbw.util.ResultGenerator;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Controller
public class MyBlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private BlogTagRelationService blogTagRelationService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private BlogLinkService blogLinkService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 首页，需要专门一个方法走缓存
     *
     * @param model
     * @return
     */
    @GetMapping({"/", "/index", "/index.html"})
    private String index(Model model) throws Exception{
        PageResult blogPageResult = null;

        blogPageResult = blogService.pageIndex(new BlogPageCondition().setPageNum(1).setPageSize(10).setPageName("首页"));


        model.addAttribute("blogPageResult", blogPageResult);
        model.addAttribute("categoryList", categoryService.getCategoryList());
        model.addAttribute("configurations", configService.getAllConfigs());
        model.addAttribute("friendLinks", blogLinkService.getAllLinks());
        model.addAttribute("hotBlogs", blogService.getNewOrHotBlogs(2, 1, 5));
        model.addAttribute("hotTags", tagService.getBlogTagCountForIndex());
        model.addAttribute("pageName", "首页");

        return "blog/amaze/index";
    }

    /**
     * 搜索
     *
     * @param model
     * @param keyword
     * @return
     */
    @GetMapping({"/search/{keyword}"})
    public String search(Model model, @PathVariable("keyword") String keyword) throws Exception {
        return this.page(model, new BlogPageCondition()
                .setPageNum(1)
                .setPageName("首页")
                .setKeyword(keyword)
        );
    }

    /**
     * 热门标签
     *
     * @param model
     * @param tagId
     * @return
     */
    @GetMapping({"/tag/{tagId}"})
    public String tag(Model model, @PathVariable("tagId") String tagId) throws Exception {
        return this.page(model, new BlogPageCondition()
                .setPageNum(1)
                .setPageName("标签")
                .setTagId(tagId));
    }

    @GetMapping("/category/{categoryName}")
    public String category(Model model, @PathVariable("categoryName") String categoryName) throws Exception {

        return this.page(model, new BlogPageCondition()
                .setPageNum(1)
                .setPageName("分类目录")
                .setCategoryName(categoryName));
    }

    /**
     * 首页的分页数据
     * 返回分页数据和页面
     *
     * @return
     */
    @GetMapping("/page")
    private String page(Model model, BlogPageCondition condition) throws Exception {

        PageResult blogPageResult = blogService.findMyBlogPage(condition);

        if (Objects.nonNull(condition.getKeyword())) {
            model.addAttribute("keyword", condition.getKeyword());
        }
        if (Objects.nonNull(condition.getTagId())) {
            model.addAttribute("tagId", condition.getTagId());
        }

        model.addAttribute("blogPageResult", blogPageResult);
        model.addAttribute("categoryList", categoryService.getCategoryList());
        model.addAttribute("configurations", configService.getAllConfigs());
        model.addAttribute("friendLinks", blogLinkService.getAllLinks());
        model.addAttribute("hotBlogs", blogService.getNewOrHotBlogs(2, 1, 5));
        model.addAttribute("hotTags", tagService.getBlogTagCountForIndex());
        model.addAttribute("pageName", condition.getPageName());

        return "blog/amaze/index";
    }

    /**
     * 点击博客封面进入博客，，博客详情页
     *
     * @param model
     * @param blogId
     * @return
     */
    @GetMapping("/blog/{blogId}")
    public String blogDetail(Model model, @PathVariable("blogId") Long blogId) throws Exception {

        //根据blogId获取Blog
        Blog blog = blogService.getById(blogId);
        if (Objects.isNull(blog)) {
            return null;
        }

        //消息队列异步，增加浏览量
        blogService.increseView(blog);

        //查询关联tagId
        List<BlogTagRelation> blogTagRelations = blogTagRelationService.list(
                new LambdaQueryWrapper<BlogTagRelation>()
                        .eq(BlogTagRelation::getBlogId, blogId));
        // 获得关联的标签列表
        List<Integer> tagIds;
        List<BlogTags> tagList = new ArrayList<>();
        if (!blogTagRelations.isEmpty()) {
            tagIds = blogTagRelations.stream()
                    .map(BlogTagRelation::getTagId).collect(Collectors.toList());
            tagList = tagService.list(new LambdaQueryWrapper<BlogTags>().in(BlogTags::getTagId, tagIds));
        }

        //查博客下评论数
        Integer blogCommentCount = commentService.count(new LambdaQueryWrapper<BlogComment>()
                .eq(BlogComment::getCommentStatus, CommentStatusEnum.ALLOW.getStatus())
                .eq(BlogComment::getDeleteStatus, DeleteStatusEnum.NO_DELETED.getStatus())
                .eq(BlogComment::getBlogId, blogId));

        BlogDetailVO blogDetailVO = new BlogDetailVO();
        BeanUtils.copyProperties(blog, blogDetailVO);
        blogDetailVO.setCommentCount(blogCommentCount);
        blogDetailVO.setBlogId(blogId);
        if (blogDetailVO != null) {
            model.addAttribute("blogDetailVO", blogDetailVO);
        }

        model.addAttribute("categoryList", categoryService.getCategoryList());
        model.addAttribute("tagList", tagList);
        model.addAttribute("pageName", "详情");
        model.addAttribute("configurations", configService.getAllConfigs());

        return "blog/amaze/detail";
    }

    /**
     * 获取博客页评论列表
     *
     * @param ajaxPutPage
     * @param blogId
     */
    @GetMapping("/blog/listComment")
    @ResponseBody
    public AjaxResultPage<BlogComment> listComment(AjaxPutPage<BlogComment> ajaxPutPage, Integer blogId) {
        Page<BlogComment> page = ajaxPutPage.putPageToPage();

        commentService.page(page, new LambdaQueryWrapper<BlogComment>()
                .eq(BlogComment::getBlogId, blogId)
                .eq(BlogComment::getCommentStatus, CommentStatusEnum.ALLOW.getStatus())
                .eq(BlogComment::getDeleteStatus, DeleteStatusEnum.NO_DELETED.getStatus())
                .orderByDesc(BlogComment::getCreateTime));

        AjaxResultPage<BlogComment> ajaxResultPage = new AjaxResultPage<>();
        ajaxResultPage.setCount(page.getTotal());
        ajaxResultPage.setData(page.getRecords());
        return ajaxResultPage;
    }

    /**
     * 提交评论
     *
     * @param request
     * @param blogComment
     * @return
     */
    @PostMapping(value = "/blog/comment")
    @ResponseBody
    public Result<String> comment(HttpServletRequest request,
                                  @Validated BlogComment blogComment) {
        String ref = request.getHeader("Referer");
        if (StringUtils.isEmpty(ref)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR, "非法请求");
        }
        // 对非法字符进行转义，防止xss漏洞
        blogComment.setCommentBody(StringEscapeUtils.escapeHtml4(blogComment.getCommentBody()));
        blogComment.setCreateTime(DateUtils.getLocalCurrentDate());
        blogComment.setCommentatorIp(requestUtil.getIpAddress(request));

        //给admin发邮件，通知有新评论
        commentService.sendMail(blogComment);

        boolean flag = commentService.save(blogComment);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }


}
