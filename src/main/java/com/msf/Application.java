package com.msf;

import com.msf.common.core.util.SpringContextHolder;
import com.msf.data.vo.DaysMatterConfigVO;
import com.msf.service.IDaysMatterService;
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
        IDaysMatterService daysMatterService = SpringContextHolder.getBean(DaysMatterServiceImpl.class);
        //开启定时任务
        DaysMatterConfigVO daysMatterConfigVO = daysMatterService.getConfig();
        daysMatterService.addJob(daysMatterConfigVO.getRegularMinute());
        daysMatterService.call(openOnStart);
    }
}
