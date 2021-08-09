package com.zbw.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.zbw.config.MailConfig;
import com.zbw.constants.*;
import com.zbw.entity.*;
import com.zbw.pojo.dto.Result;
import com.zbw.service.*;
import com.zbw.util.MD5Utils;
import com.zbw.util.ResultGenerator;
import com.zbw.util.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PushbackReader;
import java.net.URISyntaxException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private BlogLinkService blogLinkService;

    @GetMapping("/login")
    public String login(){
        return "admin/login";
    }

    /**
     * index页面，需要获取
     * 分类目录、博文、标签、评论等数量，用以显示到封面
     * @param
     * @return
     */
    @GetMapping(  "/index")
    public String index(HttpSession session) throws Exception{
        //把数量存到session域
        session.setAttribute("categoryCount", categoryService.count(
                new LambdaQueryWrapper<BlogCategory>().eq(BlogCategory::getDeleteStatus,
                        DeleteStatusEnum.NO_DELETED.getStatus())
        ));
        session.setAttribute("blogCount", blogService.count(
                new LambdaQueryWrapper<Blog>().eq(Blog::getDeleteStatus,
                        DeleteStatusEnum.NO_DELETED.getStatus())
        ));
        session.setAttribute("linkCount", blogLinkService.count(
                new LambdaQueryWrapper<BlogLink>().eq(BlogLink::getDeleteStatus,
                        DeleteStatusEnum.NO_DELETED.getStatus())
        ));
        session.setAttribute("tagCount", tagService.count(
                new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getDeleteStatus,
                        DeleteStatusEnum.NO_DELETED.getStatus())
        ));
        session.setAttribute("commentCount", commentService.count(
                new LambdaQueryWrapper<BlogComment>().eq(BlogComment::getDeleteStatus,
                        DeleteStatusEnum.NO_DELETED.getStatus())
        ));
        session.setAttribute("sysList",configService.getAllConfigsList());
        return "admin/index";
    }

    /**
     * 登陆验证
     * @param username
     * @param password
     * @param session
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public Result<String> login(String username, String password,
                                HttpSession session) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.BAD_REQUEST);
        }

        User adminUser = adminService.login(username,password);

        if (adminUser != null) {
            session.setAttribute(SessionConstants.LOGIN_USER, adminUser.getNickname());
            session.setAttribute(SessionConstants.LOGIN_USER_ID, adminUser.getUserId());
            session.setAttribute(SessionConstants.LOGIN_USER_NAME, adminUser.getUserName());
            session.setAttribute(SessionConstants.AUTHOR_IMG, configService.getById(
                    SysConfigConstants.SYS_AUTHOR_IMG.getConfigField()));
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK,"/admin/index");
        } else {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.UNAUTHORIZED);
        }
    }

    @GetMapping("welcome")
    public String welcome(){

        return "admin/welcome";
    }

    /**
     * 刷新
     * @param session
     * @return
     */
    @ResponseBody
    @GetMapping("/reload")
    public boolean reload(HttpSession session){
        Integer userId = (Integer) session.getAttribute(SessionConstants.LOGIN_USER_ID);
        return userId != null && userId != 0;
    }

    /**
     * 注销登陆
     * @param session
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "admin/login";
    }

    /**
     * 返回个人信息
     * @return
     */
    @GetMapping("/userInfo")
    public String gotoUserInfo(){
        return "admin/userInfo-edit";
    }

    @ResponseBody
    @GetMapping("/password")
    public Result<String> validatePassword(String oldPwd,HttpSession session){
        Integer userId = (Integer) session.getAttribute(SessionConstants.LOGIN_USER_ID);
        boolean flag = adminService.validatePassword(userId,oldPwd);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.BAD_REQUEST);
    }

    /**
     * 修改信息
     * @param session
     * @param userName
     * @param newPwd
     * @param nickName
     * @return
     */
    @ResponseBody
    @PostMapping("/userInfo")
    public Result<String> userInfoUpdate(HttpSession session,String userName, String newPwd,
                                         String nickName) {
        if (StringUtils.isEmpty(newPwd) || StringUtils.isEmpty(nickName)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.BAD_REQUEST);
        }
        Integer loginUserId = (int) session.getAttribute(SessionConstants.LOGIN_USER_ID);
        User adminUser = new User()
                .setUserId(loginUserId)
                .setUserName(userName)
                .setNickname(nickName)
                .setPassword(MD5Utils.MD5Encode(newPwd, "UTF-8"));
        if (adminService.updateUserInfo(adminUser)) {
            //修改成功后清空session中的数据，前端控制跳转至登录页
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK,"/admin/logout");
        } else {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 上传头像
     * @param request
     * @param file
     * @return
     * @throws URISyntaxException
     */
    @PostMapping({"/upload/authorImg"})
    @ResponseBody
    public Result<String> upload(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws URISyntaxException {
        String suffixName = UploadFileUtils.getSuffixName(file);
        //生成文件名称通用方法
        String newFileName = UploadFileUtils.getNewFileName(suffixName);
        File fileDirectory = new File(UploadConstants.UPLOAD_AUTHOR_IMG);
        //创建文件
        File destFile = new File(UploadConstants.UPLOAD_AUTHOR_IMG + newFileName);
        try {
            if (!fileDirectory.exists()) {
                if (!fileDirectory.mkdirs()) {
                    throw new IOException("文件夹创建失败,路径为：" + fileDirectory);
                }
            }
            file.transferTo(destFile);
            String sysAuthorImg = UploadConstants.SQL_AUTHOR_IMG + newFileName;
            Config blogConfig = new Config()
                    .setConfigField(SysConfigConstants.SYS_AUTHOR_IMG.getConfigField())
                    .setConfigValue(sysAuthorImg);
            configService.updateById(blogConfig);
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }



}
