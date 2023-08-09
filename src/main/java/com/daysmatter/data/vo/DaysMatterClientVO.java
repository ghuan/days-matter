package com.daysmatter.data.vo;

import lombok.Data;

import java.util.Date;

/**
 * @description 纪念日客户端展示vo
 * @author tianma
 * @date 2022/11/16 11:35
 **/
@Data//提供getter setter
public class DaysMatterClientVO {
	/**
	 * 置顶
	 **/
	private Integer top;
	/**
	 * 当天距离目标日期相差天数
	 **/
	private Integer distanceDays;
	/**
	 * 目标日期
	 **/
	private Date date;
	/**
	 * 当天距离周年相差天数
	 **/
	private Integer distanceAnniversaryDays;
	/**
	 * 第几周年
	 **/
	private Integer anniversaryNum;
	/**
	 * 周年目标日期
	 **/
	private String anniversaryDate;
	/**
	 * 当天距离百日相差天数
	 **/
	private Integer distanceHundredDays;
	/**
	 * 第几百日
	 **/
	private Integer hundredNum;
	/**
	 * 百日目标日期
	 **/
	private String hundredDate;
	/**
	 * 展示内容
	 **/
	private String content;
	/**
	 * 获取最小相差日期，用于排序
	 **/
	public Integer getMiniSortDays(){
		if(this.distanceDays == null){
			return null;
		}
		if(this.distanceAnniversaryDays == null && this.distanceHundredDays == null){
			return this.distanceDays;
		}else {
			return this.distanceAnniversaryDays == null ? this.distanceHundredDays
					: (this.distanceHundredDays == null ? this.distanceAnniversaryDays
					: (this.distanceAnniversaryDays <= this.distanceHundredDays ? this.distanceAnniversaryDays : this.distanceHundredDays));
		}
	}
}
