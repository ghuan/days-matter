package com.msf.data.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页类
 */
@Data
public class PageVO<T> {
	//总共的数据条数
	private Long totalCount;
	//第几页
	private Integer pageNo;
	//每页显示多少
	private Integer pageSize;
	//查询出来的数据
	private List<T> data;
}
