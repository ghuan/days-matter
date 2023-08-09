package com.daysmatter.enums;

import cn.hutool.core.date.DateUtil;
import com.daysmatter.util.LunarSolarConverter;

import java.util.Calendar;
import java.util.Date;
import java.util.function.BiFunction;

/**
 * @description 不同重复类型计算间隔天数
 * @author tianma
 * @date 2022/11/18 16:34
 **/
public enum RepeatDateStrategyEnum {

    /**
     * 不重复
     **/
    NO_REPEAT(0, (d,dt) -> d),
    /**
     * 每天重复
     **/
    REPEAT_DAY(1, (d,dt) -> DateUtil.offsetDay(d,1)),
    /**
     * 每周重复
     **/
    REPEAT_WEEK(2,  (d,dt) -> DateUtil.offsetWeek(d,1)),
    /**
     * 每月重复
     **/
    REPEAT_MONTH(3, (d,dt) -> DateUtil.offsetMonth(d,1)),
    /**
     * 每年重复
     **/
    REPEAT_YEAR(4, (d,dt) -> {
        if(dt){
            //农历
            LunarSolarConverter.Solar solar = new LunarSolarConverter.Solar();
            String[] ds = DateUtil.formatDate(d).split("-");
            solar.solarYear = Integer.parseInt(ds[0]);
            solar.solarMonth = Integer.parseInt(ds[1]);
            solar.solarDay = Integer.parseInt(ds[2]);
            LunarSolarConverter.Lunar lunar = LunarSolarConverter.SolarToLunar(solar);
            lunar.lunarYear = lunar.lunarYear+1;
            LunarSolarConverter.Solar solarNew = LunarSolarConverter.LunarToSolar(lunar);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR,solarNew.solarYear);
            c.set(Calendar.MONTH,solarNew.solarMonth-1);
            c.set(Calendar.DAY_OF_MONTH,solarNew.solarDay);
            return DateUtil.parseDate(DateUtil.formatDate(c.getTime()));
        }
        return DateUtil.offsetMonth(d,12);
    });

    private Integer repeatType;
    private BiFunction<Date,Boolean,Date> repeatFunction;
    RepeatDateStrategyEnum(Integer repeatType, BiFunction<Date,Boolean,Date> repeatFunction){
        this.repeatType = repeatType;
        this.repeatFunction = repeatFunction;
    }
    /**
     * 根据matchType获取enum
     **/
    public static RepeatDateStrategyEnum getByRepeatType(Integer repeatType) {
        for(RepeatDateStrategyEnum e : values()){
            if(e.repeatType.equals(repeatType)){
                return e;
            }
        }
        return null;
    }
    /**
     * 入口
     **/
    public Date getDateOnce(Date date,Boolean dt){
       return this.repeatFunction.apply(date,dt);
    }
}
