package com.daysmatter.data.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @description 纪念日配置表
 * @author tianma
 * @date 2022/11/16 11:35
 **/
@Entity
@Table(name = "days_matter_config")
@Data//提供getter setter
public class DaysMatterConfig {
	/**
	 * 主键
	 **/
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="MySequenceGenerator")
//	@GenericGenerator(name = "MySequenceGenerator", strategy = MySequenceGenerator.classPath)
	private Long id;
	/**
	 * 定时提醒间隔分钟数
	 **/
	@Column(name="regular_minute")
	private Integer regularMinute;
	/**
	 * 定时提醒先决条件-剩余阈值天数的纪念日
	 **/
	@Column(name="threshold_days")
	private Integer thresholdDays;
	/**
	 * 客户端-宽度
	 **/
	@Column(name="client_width")
	private Integer clientWidth;
	/**
	 * 客户端-高度
	 **/
	@Column(name="client_height")
	private Integer clientHeight;
}
