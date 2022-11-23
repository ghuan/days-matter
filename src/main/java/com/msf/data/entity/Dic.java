package com.msf.data.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * 字典实体类
 *
 * @since 2018-03-12
 */
@Entity
@IdClass(DicPK.class)
@Table(name = "SYS_DIC")
@Data//提供getter setter
public class Dic {
	@Id
	private String DIC_CODE;
	@Id
	private String CODE;
	private String DIC_NAME;
	private String NAME;
	private String PARENT_CODE;
	private String PY;
	private String WB;
	private Integer SORT;
	private String CODE1;
	private String CODE2;
}
