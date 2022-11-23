package com.msf.service;

import java.util.List;
import java.util.Map;

/**
 *
 * description：SchemaService接口
 */
public interface ISchemaUtilService {
	Map<String, Object> getSchema(Map<String,Object> req);
	Map<String, Object> getSchema(String schemaCode);
	List<Map<String, Object>> getSchemaDataItems(String schemaCode);
	List<Map<String, Object>> getSchemaDataItems(Map<String,Object> schema);
	Map<String,Object> getLocalSchema(String schemaCode);
}
