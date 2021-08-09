package com.zbw.interceptor;


import com.zbw.constants.SessionConstants;
import com.zbw.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 后台登陆拦截处理器
 */
@Component
public class AdminLoginInterceptor implements HandlerInterceptor {

    private static final Pattern pattern = Pattern.compile("/admin\\b");

    @Autowired
    private AdminService adminService;

    /**
     * 拦截uri处理【预处理】
     * 用户跨域验证
     *
     * @param request
     * @param response
     * @param handler
     * @return boolean
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        //这个请求头是封装过的token
        //如果uri是/admin，

        if (pattern.matcher(uri).find() &&
                Objects.isNull(request.getSession().getAttribute(SessionConstants.LOGIN_USER_ID))) {
            request.getSession().setAttribute("errorMsg", "请重新登陆");
            System.out.println(request.getContextPath());
            response.sendRedirect(request.getContextPath()+"/admin/login");
            return false;
        } else {
            request.getSession().removeAttribute("errorMsg");
            return true;
        }
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
