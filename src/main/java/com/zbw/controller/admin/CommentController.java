package com.zbw.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zbw.constants.HttpStatusEnum;
import com.zbw.entity.BlogComment;
import com.zbw.pojo.dto.AjaxPutPage;
import com.zbw.pojo.dto.AjaxResultPage;
import com.zbw.pojo.dto.Result;
import com.zbw.pojo.vo.BlogCommentVo;
import com.zbw.service.CommentService;
import com.zbw.util.DateUtils;
import com.zbw.util.PageQueryUtil;
import com.zbw.util.ResultGenerator;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class CommentController {


    @Autowired
    private CommentService commentService;


    @GetMapping("/comment")
    public String gotoComment(){
        return "admin/comment-list";
    }

    /**
     * 分页获取评论列表
     * @param ajaxPutPage
     * @param
     * @return
     */
    @ResponseBody
    @GetMapping("/comment/paging")
    public AjaxResultPage<BlogCommentVo> getCommentList(AjaxPutPage<BlogCommentVo> ajaxPutPage, @RequestParam(value = "blogId",required = false) Long blogId){

        List<BlogCommentVo> pageList = commentService.findAndPage(ajaxPutPage, blogId);
        AjaxResultPage<BlogCommentVo> result = new AjaxResultPage<>();
        result.setData(pageList);
        result.setCount(commentService.count(new LambdaQueryWrapper<BlogComment>()
                .eq(Objects.nonNull(blogId),BlogComment::getBlogId,blogId)));
        return result;
    }

    /**
     * 更新评论状态
     * @param blogComment
     * @return
     */
    @ResponseBody
    @PostMapping("/comment/commentStatus")
    public Result<String > updateCommentStatus(BlogComment blogComment){
        boolean flag = commentService.updateById(blogComment);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 删除评论，指的是清除
     * @param commentId
     * @return
     */
    @PostMapping("/comment/delete")
    @ResponseBody
    public Result<String> deleteComment(@RequestParam Long commentId){
        boolean flag = commentService.removeById(commentId);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 修改某条评论的数据，【目前是用来回复】
     * @param blogComment
     * @return
     */
    @ResponseBody
    @PostMapping("/comment/edit")
    public Result<String> editComment(BlogComment blogComment){

        if(blogComment.getCommentStatus() != 1){
            return ResultGenerator.getFailResult(400,"评论审核未通过!");
        }

        blogComment.setReplyTime(DateUtils.getLocalCurrentDate());
        blogComment.setReplyBody(StringEscapeUtils.escapeHtml4(blogComment.getReplyBody()));

        commentService.sendMail(blogComment);

        boolean flag = commentService.updateById(blogComment);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }else{
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }


}