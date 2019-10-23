package com.egovalley.web.interceptor;

import com.egovalley.domain.EgoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 获取url
            String url = request.getRequestURI();
            logger.info(">>> 拦截url = " + url);
            // 获取session
            HttpSession session = request.getSession();
            EgoUser user = (EgoUser) session.getAttribute("userInfo");
            // 判断开头路径, 跳转至登录页
            if (url.contains("egovalley") && user == null) {
                logger.info(">>> 未登录, 拦截: " + url);
                response.sendRedirect(request.getContextPath() + "/msg");
                return false;
            }
            if (user != null) {
                logger.info(">>> 已登录, 放行: " + url);
                return true;
            }
            response.sendRedirect(request.getContextPath() + "/msg");
            return false;
        } catch (Exception e) {
            logger.error(">>> 登录拦截出错", e);
            response.sendRedirect(request.getContextPath() + "/msg");
            return false;
        }
    }

}
