package com.daysmatter.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 转发代理配置
 *
 * @author 22234
 * @date 2023-08-08 17:29:12
 */
@Configuration
public class ProxyFilterConfig {

    @Bean
    public FilterRegistrationBean registrationProjectFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new ProxyFilter());
        registration.addUrlPatterns("/api/*");
        return registration;
    }

    private class ProxyFilter implements Filter {

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {

        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String requestURL = httpServletRequest.getRequestURI();
            String realRequestUrl = requestURL.replace("/api", "");
            servletRequest.getRequestDispatcher(realRequestUrl).forward(servletRequest, servletResponse);
        }

        @Override
        public void destroy() {

        }
    }
}