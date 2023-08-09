package com.daysmatter.data.vo;

import lombok.Data;

/**
 * @description 纪念日配置表
 * @author tianma
 * @date 2022/11/16 11:35
 **/
@Data//提供getter setter
public class DaysMatterConfigVO {
	private Long id;
	/**
	 * 定时提醒间隔分钟数
	 **/
	private Integer regularMinute;
	/**
	 * 定时提醒先决条件-剩余阈值天数的纪念日
	 **/
	private Integer thresholdDays;
	/**
	 * 客户端-宽度
	 **/
	private Integer clientWidth;
	/**
	 * 客户端-高度
	 **/
	private Integer clientHeight;
}
