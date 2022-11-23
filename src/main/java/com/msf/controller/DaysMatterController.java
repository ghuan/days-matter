package com.msf.controller;

import com.msf.data.po.DaysMatterConfigPO;
import com.msf.data.vo.PageVO;
import com.msf.service.IDaysMatterService;
import com.msf.data.vo.DaysMatterConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 纪念日接口
 *
 * @author tianma
 * @date 2022/ 11/16 10:07:36
 */
@RestController
@RequestMapping("/daysMatter")
public class DaysMatterController {
    @Autowired
    private IDaysMatterService daysMatterService;

    @PostMapping("doQuery")
    public PageVO<Map<String,Object>> doQuery(@RequestBody Map req){
        return daysMatterService.doQuery(req);
    }
    @PostMapping("doSave")
    public Map<String,Object> doSave(@RequestBody Map req){
        return daysMatterService.doSave(req);
    }

    @PostMapping("getConfig")
    public DaysMatterConfigVO getConfig(){
        return daysMatterService.getConfig();
    }

    @PostMapping("saveConfig")
    public void saveConfig(@RequestBody DaysMatterConfigPO daysMatterConfigPO){
        daysMatterService.saveConfig(daysMatterConfigPO);
    }

    @PostMapping("getLunarDate")
    public String getLunarDate(@RequestBody Map req){
        return daysMatterService.getLunarDate(req);
    }

}
