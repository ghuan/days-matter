package com.msf.schedule;

import com.msf.common.core.util.SpringContextHolder;
import com.msf.service.IDaysMatterService;
import com.msf.service.imp.DaysMatterServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务
 *
 * @author tianma
 * @date 2022/ 11/24 11:23:15
 */
@Slf4j
public class DaysMatterJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context){
        log.info("===================");
        IDaysMatterService daysMatterService = SpringContextHolder.getBean(DaysMatterServiceImpl.class);
        daysMatterService.call(false);
    }
}