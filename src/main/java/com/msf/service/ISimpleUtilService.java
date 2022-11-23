package com.msf.service;

import java.util.List;
import java.util.Map;

/**
 *
 * description：simple service接口
 */
public interface ISimpleUtilService {
	Map<String, Object> simpleQuery(Map req);
	Map<String, Object> simpleLoad(Map req);
	Map<String, Object> simpleSave(Map req);
	void simpleRemove(Map req);
	Map<String, Object> addDicText(Map req);
	String getQueryHQL(String schemaCode, List<Object> cnd,String sort);
	String getQueryHQL(Map<String,Object> schema, List<Object> cnd,String sort);
}
