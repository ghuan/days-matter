package com.daysmatter.controller;

import com.daysmatter.data.dto.DaysMatterConfigDTO;
import com.daysmatter.data.dto.DaysMatterDTO;
import com.daysmatter.data.vo.DaysMatterConfigVO;
import com.daysmatter.data.vo.DaysMatterVO;
import com.daysmatter.service.IDaysMatterService;
import com.gh.framework.common.web.data.PageVO;
import com.gh.framework.common.web.data.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 纪念日接口
 *
 * @author tianma
 * @date 2022/ 11/16 10:07:36
 */
@RestController
@RequestMapping("/daysMatter")
public class DaysMatterController {

    @Resource
    private IDaysMatterService daysMatterService;

    @GetMapping("getPage")
    public R<PageVO<DaysMatterVO>> getPage(@Valid DaysMatterDTO daysMatterDTO){
        return R.success(daysMatterService.getPage(daysMatterDTO));
    }
    @PostMapping("save")
    public R<DaysMatterVO>  save(@Valid @RequestBody DaysMatterDTO daysMatterDTO){
        return R.success(daysMatterService.save(daysMatterDTO));
    }

    @GetMapping("delete")
    public R<Boolean> delete(@NotEmpty @RequestParam List<Long> ids){
        return R.success(daysMatterService.delete(ids));
    }

    @GetMapping("getConfig")
    public R<DaysMatterConfigVO> getConfig(){
        return R.success(daysMatterService.getConfig());
    }

    @PostMapping("saveConfig")
    public R<Boolean> saveConfig(@RequestBody @Valid DaysMatterConfigDTO daysMatterConfigDTO){
        daysMatterService.saveConfig(daysMatterConfigDTO);
        return R.success(true);
    }
    @GetMapping("getLunarDate")
    public R<String> getLunarDate(@RequestParam String date){
        return R.success(daysMatterService.getLunarDate(date));
    }

}
