package com.msf.data.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 字典明细主键类
 *
 * @since 2018-03-12
 */
@Data
public class DicPK implements Serializable {
	private String DIC_CODE;
	private String CODE;
}
