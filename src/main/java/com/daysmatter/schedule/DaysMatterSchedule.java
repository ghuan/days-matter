package com.daysmatter.schedule;

import com.daysmatter.data.vo.DaysMatterConfigVO;
import com.daysmatter.service.IDaysMatterService;
import com.gh.framework.common.core.annotation.async.EnableGhAsync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * 定时任务
 */
@Component
@EnableScheduling
@EnableGhAsync
@Slf4j
public class DaysMatterSchedule {
    @Resource
    private IDaysMatterService daysMatterService;

    private Long lastExecuteTimeMillis;
    private Integer regularMinute;

    public void setRegularMinute(Integer regularMinute){
        this.regularMinute = regularMinute;
        lastExecuteTimeMillis = System.currentTimeMillis();
    }

    @Scheduled(cron = "0/30 * * * * ?")
    @Transactional(rollbackFor = {Exception.class})
    public void execute(){
        if(lastExecuteTimeMillis == null){
            lastExecuteTimeMillis = System.currentTimeMillis();
        }
        Long currentTimeMillis = System.currentTimeMillis();
        if(regularMinute == null){
            //默认三十分钟提醒
            regularMinute = 30;
            DaysMatterConfigVO  daysMatterConfigVO = daysMatterService.getConfig();
            if(daysMatterConfigVO != null){
                regularMinute = daysMatterConfigVO.getRegularMinute();
            }
        }
        //加五秒程序执行误差
        if((currentTimeMillis - lastExecuteTimeMillis + 5000)/60000 >= regularMinute){
            daysMatterService.call(false);
            lastExecuteTimeMillis = currentTimeMillis;
        }
    }
}
