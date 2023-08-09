package com.daysmatter.data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gh.framework.common.web.data.PageParam;
import lombok.Data;

import java.util.Date;

/**
 * @description 纪念日
 * @author tianma
 * @date 2022/11/16 11:35
 **/
@Data//提供getter setter
public class DaysMatterDTO extends PageParam {
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
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date date;
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
