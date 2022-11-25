package com.msf.schedule;

import com.msf.dao.IBaseDao;
import com.msf.data.entity.DaysMatterConfig;
import com.msf.service.IDaysMatterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * 定时任务
 */
@Component
@EnableScheduling
@EnableAsync
@Slf4j
public class DaysMatterSchedule {
    @Autowired
    private IDaysMatterService daysMatterService;
    @Autowired
    private IBaseDao dao;

    private Long lastExecuteTimeMillis;
    private Integer regularMinute;

    public void setRegularMinute(Integer regularMinute){
        this.regularMinute = regularMinute;
        lastExecuteTimeMillis = System.currentTimeMillis();
    }

    @Scheduled(cron = "0/30 * * * * ?")
    @Transactional(rollbackFor = {Exception.class})
    public void execute(){
        log.info("=====================================");
        if(lastExecuteTimeMillis == null){
            lastExecuteTimeMillis = System.currentTimeMillis();
        }
        Long currentTimeMillis = System.currentTimeMillis();
        if(regularMinute == null){
            //默认三十分钟提醒
            regularMinute = 30;
            DaysMatterConfig daysMatterConfig = dao.doLoad("from DaysMatterConfig",null);
            if(daysMatterConfig != null){
                regularMinute = daysMatterConfig.getRegularMinute();
            }
        }
        //加五秒程序执行误差
        if((currentTimeMillis - lastExecuteTimeMillis + 5000)/60000 >= regularMinute){
            log.info("=====================================");
            daysMatterService.call(false);
            lastExecuteTimeMillis = currentTimeMillis;
        }
    }
}
