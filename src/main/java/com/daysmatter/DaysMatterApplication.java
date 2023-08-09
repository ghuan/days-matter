package com.daysmatter;

import com.daysmatter.service.IDaysMatterService;
import com.daysmatter.service.imp.DaysMatterServiceImpl;
import com.gh.framework.common.core.spring.context.holder.SpringContextHolder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class DaysMatterApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(DaysMatterApplication.class)
                .headless(false).run(args);
        Environment ev = SpringContextHolder.getBean(Environment.class);
        Boolean openOnStart = "true".equals(ev.getProperty("client.open-on-start"));
        IDaysMatterService daysMatterService = SpringContextHolder.getBean(DaysMatterServiceImpl.class);
        daysMatterService.call(openOnStart);
    }
}
