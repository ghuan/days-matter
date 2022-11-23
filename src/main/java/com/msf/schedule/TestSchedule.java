package com.msf.schedule;

import com.msf.DaysMatterJDialog;
import com.msf.dao.IBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * spring schedule 定时任务例子
 */
@Component
@Transactional(readOnly = true)
@EnableScheduling
@EnableAsync
public class TestSchedule {
    @Autowired
    private DaysMatterJDialog daysMatterJDialog;
    @Autowired
    private IBaseDao dao;

    @Scheduled(cron = "0 40 17 * * ?")
    @Transactional(rollbackFor = {Exception.class})
    public void execute(){
        daysMatterJDialog.doShow();
    }
}
