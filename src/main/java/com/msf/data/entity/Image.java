package com.msf.data.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 图片表
 *
 * @since 2018-03-12
 */
@Entity
@Table(name = "SYS_IMAGE")
@Data//提供getter setter
public class Image {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="MySequenceGenerator")
//	@GenericGenerator(name = "MySequenceGenerator", strategy = MySequenceGenerator.classPath)
	private Long IMAGE_ID;
	private String NAME;
	private byte[] CONTENT;
	private String CONTENT_TYPE;
}
