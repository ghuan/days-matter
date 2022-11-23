package com.msf.schedule.config;

import com.msf.service.IDaysMatterService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 动态定时任务配置
 *
 * @author tianma
 * @date 2022/ 11/18 10:19:56
 */
@Data
@Slf4j
@Component
public class ScheduleConfig implements SchedulingConfigurer {
    //todo 默认三十分钟触发一次
    private String cron = "0 0/30 * * * ?";
    @Autowired
    private IDaysMatterService daysMatterService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(() -> {
            daysMatterService.call(false);
        }, triggerContext -> {
            // 使用CronTrigger触发器，可动态修改cron表达式来操作循环规则
            CronTrigger cronTrigger = new CronTrigger(cron);
            Date nextExecutionTime = cronTrigger.nextExecutionTime(triggerContext);
            return nextExecutionTime;
        });
    }
}
