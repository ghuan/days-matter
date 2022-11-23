package com.msf.service;

import com.msf.common.core.dto.AgeDTO;

import java.util.Map;

/**
 *
* description：simple service接口
 */
public interface ISimpleService {
	AgeDTO getAge(String birthday);
	Map<String, Object> getCode(Map req);
	String getServerDate();
	Map<String,Object> executeSQL(Map req);
	Map<String,Object> executeSQLRemote(Map req);
}
