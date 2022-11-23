package com.msf.data.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @description 纪念日表
 * @author tianma
 * @date 2022/11/16 11:35
 **/
@Entity
@Table(name = "days_matter")
@Data//提供getter setter
public class DaysMatter {
	/**
	 * 主键
	 **/
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="MySequenceGenerator")
//	@GenericGenerator(name = "MySequenceGenerator", strategy = MySequenceGenerator.classPath)
	private Long id;
	/**
	 * 标题
	 **/
	private String title;
	/**
	 * 日期类型：1 公历 2 农历
	 **/
	@Column(name="date_type")
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
	@Column(name="big_day")
	private Integer bigDay;
	/**
	 * 是否包含起始日期（+1）
	 **/
	@Column(name="contain_begin_date")
	private Integer containBeginDate;
}
