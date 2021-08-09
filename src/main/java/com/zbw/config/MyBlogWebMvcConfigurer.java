package com.zbw.config;

import com.zbw.constants.UploadConstants;
import com.zbw.interceptor.AdminLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyBlogWebMvcConfigurer implements WebMvcConfigurer {

    //admin登陆拦截器，内含处理方法
    @Autowired
    private AdminLoginInterceptor adminLoginInterceptor;

    /**
     * 添加一个拦截器，用来拦截/admin前缀的uri
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册一个拦截器，拦截以/admin为前缀的url路径
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login")//排除该路径
                .excludePathPatterns("/admin/reload")
                .excludePathPatterns("/admin/forgetPwd")
                .excludePathPatterns("/admin/dist/**")
                .excludePathPatterns("/admin/plugins/**")
                .excludePathPatterns("/X-admin/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/authorImg/**").addResourceLocations("file:" + UploadConstants.UPLOAD_AUTHOR_IMG);
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + UploadConstants.FILE_UPLOAD_DIC);
    }


}
