package com.msf;

import com.msf.common.core.util.SpringContextHolder;
import com.msf.service.imp.DaysMatterServiceImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class Application{
    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .headless(false).run(args);
        Environment ev = SpringContextHolder.getBean(Environment.class);
        Boolean openOnStart = "true".equals(ev.getProperty("client.openOnStart"));
        //打开客户端
        SpringContextHolder.getBean(DaysMatterServiceImpl.class).call(openOnStart);
        //关闭c#的启动画面


    }
}
