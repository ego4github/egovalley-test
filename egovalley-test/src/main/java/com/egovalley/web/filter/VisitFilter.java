package com.egovalley.web.filter;

import com.egovalley.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 统计访问流量的过滤器
 */
@WebFilter(filterName = "visitCountFilter", urlPatterns = {"/", "/login"})
@Order(1)// 指定过滤器的执行顺序, 值越大越靠后执行
public class VisitFilter implements Filter {

    @Value("${count.daily.path}")
    private String dailyPath;
    @Value("${count.total.path}")
    private String totalPath;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
        IOUtils.addCount(dailyPath, "" + (Integer.parseInt(IOUtils.readCount(dailyPath)) + 1));
//        IOUtils.addCount(totalPath, "" + (Integer.parseInt(IOUtils.readCount(totalPath)) + 1));
    }

    @Override
    public void destroy() {

    }

}
