package com.msf.data.dto;

import lombok.Data;

/**
 * @description 纪念日
 * @author tianma
 * @date 2022/11/16 11:35
 **/
@Data//提供getter setter
public class DaysMatterDTO {
	/**
	 * 主键
	 **/
	private Long id;
	/**
	 * 标题
	 **/
	private String title;
	/**
	 * 日期类型：1 公历 2 农历
	 **/
	private Integer dateType;
	/**
	 * 目标日期
	 **/
	private String date;
	/**
	 * 是否置顶
	 **/
	private Integer top;
	/**
	 * 重复
	 **/
	private Integer repeat;
	/**
	 * bigDay 周年和百日提醒
	 **/
	private Integer bigDay;
	/**
	 * 是否包含起始日期（+1）
	 **/
	private Integer containBeginDate;
}
