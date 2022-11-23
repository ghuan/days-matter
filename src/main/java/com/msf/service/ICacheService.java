package com.msf.service;

/**
 *
* description：cacheService接口
 */
public interface ICacheService {
	String removeSchemaCache(String schemaCode);
	String removeDicCache(String dicCode);
}
