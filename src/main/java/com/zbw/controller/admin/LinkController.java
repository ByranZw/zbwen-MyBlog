package com.zbw.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zbw.constants.HttpStatusEnum;
import com.zbw.constants.LinkConstants;
import com.zbw.entity.BlogLink;
import com.zbw.pojo.dto.AjaxPutPage;
import com.zbw.pojo.dto.AjaxResultPage;
import com.zbw.pojo.dto.Result;
import com.zbw.service.BlogLinkService;
import com.zbw.util.DateUtils;
import com.zbw.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class LinkController {


    @Autowired
    private BlogLinkService blogLinkService;

    @GetMapping("/linkType")
    public String gotoLink(){
        return "admin/link-list";
    }

    @ResponseBody
    @GetMapping("/linkType/list")
    public Result<List<BlogLink>> linkTypeList(){
        List<BlogLink> links = new ArrayList<>();
        links.add(new BlogLink().setLinkType(LinkConstants.LINK_TYPE_FRIENDSHIP.getLinkTypeId())
                .setLinkName(LinkConstants.LINK_TYPE_FRIENDSHIP.getLinkTypeName()));
        links.add(new BlogLink().setLinkType(LinkConstants.LINK_TYPE_RECOMMEND.getLinkTypeId())
                .setLinkName(LinkConstants.LINK_TYPE_RECOMMEND.getLinkTypeName()));
        links.add(new BlogLink().setLinkType(LinkConstants.LINK_TYPE_PRIVATE.getLinkTypeId())
                .setLinkName(LinkConstants.LINK_TYPE_PRIVATE.getLinkTypeName()));
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK,links);
    }

    @ResponseBody
    @GetMapping("/link/paging")
    public AjaxResultPage<BlogLink> getLinkList(AjaxPutPage<BlogLink> ajaxPutPage, BlogLink condition){
        QueryWrapper<BlogLink> queryWrapper = new QueryWrapper<>(condition);
        queryWrapper.lambda()
                .orderByAsc(BlogLink::getLinkRank);
        Page<BlogLink> page = ajaxPutPage.putPageToPage();
        blogLinkService.page(page,queryWrapper);
        AjaxResultPage<BlogLink> result = new AjaxResultPage<>();
        result.setData(page.getRecords());
        result.setCount(page.getTotal());
        return result;
    }

    @ResponseBody
    @PostMapping("/link/isDel")
    public Result<String> updateLinkStatus(BlogLink blogLink){
        boolean flag = blogLinkService.updateById(blogLink);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    @ResponseBody
    @PostMapping("/link/clear")
    public Result<String> clearLink(Integer linkId){
        boolean flag = blogLinkService.removeById(linkId);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/link/edit")
    public String editLink(Integer linkId, Model model){
        if (linkId != null){
            BlogLink blogLink = blogLinkService.getById(linkId);
            model.addAttribute("blogLink",blogLink);
        }
        return "admin/link-edit";
    }

    @ResponseBody
    @PostMapping("/link/edit")
    public Result<String> updateAndSaveLink(BlogLink blogLink){
        blogLink.setCreateTime(DateUtils.getLocalCurrentDate());
        boolean flag;
        if (blogLink.getLinkId() != null){
            flag = blogLinkService.updateById(blogLink);
        }else{
            flag = blogLinkService.save(blogLink);
        }
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }


    /**
     * 更新缓存  手动
     * @return
     */
    @ResponseBody
    @GetMapping("/link/clearCache")
    public boolean clearCache() throws Exception{

        blogLinkService.setLinkCache();
        return true;
    }


}
