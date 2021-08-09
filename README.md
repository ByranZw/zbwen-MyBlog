# 工程简介
- 本项目作为我这个新手的第一个实战练手项目，我把它部署在了[做不完の笔记](http://zbwen.cn)，其中做的一些修改可以参考置顶的博客开发日志
- 本项目是基于南街的[My-Blog-layui](https://github.com/ZHENFENG13/My-Blog-layui)
做的二次开发
- 在原先的基础上整合了redis，rabbitmq，以及mybatis-plus，并对后端逻辑进行重构
- 并按照自己的意愿进行了一些界面修改
- 项目使用技术栈
    - springboot 2.3.7-RELEASE
    - springboot-mail
    - redis
    - rabbitmq
    - thymeleaf
    - mysql
    - mybatis-plus
    - layui
    
### 项目总结
- 本系统总体架构实施逻辑还算清楚，但是对于自己的界面审美以及实操有些跟不上，有些界面美化还不会做
- 后端设计上还有一些冗余，有一些冗余的实体类，且分包分结构有些不清楚，不过问题不大。
- 实体关系还存在一些冗余字段，以及一些未开发的功能（不重要）
- 项目还存在一定的bug，还有很大的优化空间
### 效果预览
马赛克是CDN链接

- 前台首页
![](https://qncdn.zbwen.top//blogblogindex.png)
- 文章详情
![](https://qncdn.zbwen.top//blogblogdetail.png)
- 评论板块
![](https://qncdn.zbwen.top//blogblogcomment.png)
- 后台首页
![](https://qncdn.zbwen.top//blogadminwelcome.png)
- 文章编辑
![](https://qncdn.zbwen.top//blogadminwelcome.png)
- 文章列表
![](https://qncdn.zbwen.top//blogbloglist.png)
- 评论列表
![](https://qncdn.zbwen.top//blogcommentlist.png)
- 标签列表
![](https://qncdn.zbwen.top//blogtaglist.png)
- 分类目录列表
![](https://qncdn.zbwen.top//blogcategorylist.png)
- 系统配置列表
![](https://qncdn.zbwen.top//blogconfiglist.png)
- 友链列表
![](https://qncdn.zbwen.top//bloglinklist.png)
  
--- 
联系邮箱：1065936727@qq.com