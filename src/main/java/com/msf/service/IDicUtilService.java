package com.msf.service;

import com.msf.common.core.component.ValidateResult;

import java.util.List;
import java.util.Map;

/**
 *
 * description：DicService接口
 */
public interface IDicUtilService {
	/**
	 * 验证数据中所有字典key值是否匹配
	 * @param schemaCode
	 * @param data
	 */
	ValidateResult validateDicKey(String schemaCode, List<Map<String, Object>> data);

	/**
	 * 验证数据中所有字典key值是否匹配
	 * @param schemaCode
	 * @param data
	 */
	ValidateResult validateDicKey(String schemaCode, Map<String, Object> data);

	/**
	 * 验证数据中所有字典key值是否匹配
	 * @param schemaDataItems
	 * @param data
	 */
	ValidateResult validateDicKey(List<Map<String, Object>> schemaDataItems, List<Map<String, Object>> data);

	/**
	 * 验证数据中所有字典key值是否匹配
	 * @param schemaCode
	 * @param data
	 */
	ValidateResult validateDicKey(String schemaCode, List<Map<String, Object>> data, boolean throwable);

	/**
	 * 验证数据中所有字典key值是否匹配
	 * @param schemaCode
	 * @param data
	 */
	ValidateResult validateDicKey(String schemaCode, Map<String, Object> data, boolean throwable);

	/**
	 * 验证数据中所有字典key值是否匹配
	 * @param schemaDataItems
	 * @param data
	 */
	ValidateResult validateDicKey(List<Map<String, Object>> schemaDataItems, List<Map<String, Object>> data, boolean throwable);
	/**
	 * 前台设置data字典
	 * @param req
	 * @return
	 */
	Map<String, Object> addDicText(Map<String, Object> req);

	/**
	 * 根据schemaCode指定的schema中配置的items dic， 添加list map中对应key的字典text
	 * @param schemaCode
	 * @param data
	 * @return
	 */
	List<Map<String, Object>> addDicText(String schemaCode, List<Map<String, Object>> data);

	/**
	 * 根据schemaCode指定的schema中配置的items dic， 添加list map中对应key的字典text
	 * @param schemaCode
	 * @param data
	 * @return
	 */
	Map<String, Object> addDicText(String schemaCode, Map<String, Object> data);
	/**
	 * 根据schema items 中配置的items dic， 添加list map中对应key的字典text
	 * @param schemaDataItems
	 * @param data
	 * @return
	 */
	List<Map<String, Object>> addDicText(List<Map<String, Object>> schemaDataItems, List<Map<String, Object>> data);
	/**
	 * 根据dicCode 获取字典数据
	 * @param dicCode
	 * @return
	 */
	List<Map<String, Object>> getDicData(String dicCode);
	/**
	 * 根据schema 配置的dic属性 获取字典数据
	 * @param schemaDicCfg 包括两个属性 item_dic_id：id，item_dic_cnd：cnd
	 * @return
	 */
	List<Map<String, Object>> getDicData(Map<String, Object> schemaDicCfg);
	/**
	 * 获取简单字典数据，类型Map<key,text>
	 * @return
	 */
	Map<String, Object> getSimpleDicData(String dicCode);
	/**
	 * 获取简单字典数据，类型Map<key,text>
	 * @return
	 */
	Map<String, Object> getSimpleDicData(Map<String, Object> schemaDicCfg);

	/**
	 * 获取指定key的字典记录
	 * @param key
	 * @return
	 */
	Map<String,Object> getDicRecord(String dicCode,String key);
	/**
	 * 获取指定key的字典记录
	 * @param key
	 * @return
	 */
	Map<String,Object> getDicRecord(Map<String, Object> schemaDicCfg,String key);

	/**
	 * 删除指定tableName 的redis缓存字典
	 * @param tableName
	 */
	void deleteDicByTableName(String tableName);

	/**
	 * 获取本地dic配置文件
	 * @param dicCode
	 * @return
	 */
	Object getLocalDic(String dicCode);


}
