package com.msf.jpa.hibernate.core;

import com.msf.common.core.constant.CommonConstants;
import org.hibernate.MappingException;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.util.Properties;

public class MySequenceGenerator extends SequenceStyleGenerator {
	//当前类路径
	public static final String classPath = "com.msf.jpa.hibernate.core.MySequenceGenerator";

	/**
	 * 重写配置方法，自定义序列命名
	 * @param type
	 * @param params
	 * @param serviceRegistry
	 * @throws MappingException
	 */
	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		String tableName = params.get(TABLE).toString();
		params.setProperty(SEQUENCE_PARAM, CommonConstants.SEQUENCE_PREFIX+tableName);
		super.configure(type,params,serviceRegistry);
	}
}
