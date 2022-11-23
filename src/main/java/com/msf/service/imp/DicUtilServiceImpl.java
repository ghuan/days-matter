package com.msf.service.imp;

import com.msf.cache.MemoryCache;
import com.msf.common.core.component.ValidateResult;
import com.msf.common.core.constant.CacheNameConstants;
import com.msf.common.core.constant.DicConfigConstants;
import com.msf.common.core.constant.DicDataConstants;
import com.msf.common.core.constant.SchemaConfigConstants;
import com.msf.common.core.dto.ResourceDTO;
import com.msf.common.core.exception.BusinessException;
import com.msf.common.core.util.DBFunctionUtils;
import com.msf.common.core.util.RedisScan;
import com.msf.common.core.util.TreeConverter;
import com.msf.common.core.util.VTools;
import com.msf.common.core.util.gson.GsonUtils;
import com.msf.util.cnd.CndAnalysis;
import com.msf.dao.IBaseDao;
import com.msf.service.IDicUtilService;
import com.msf.service.ISchemaUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DicUtilServiceImpl implements IDicUtilService {

	@Autowired
	private IBaseDao dao;
	@Autowired
	private ISchemaUtilService schemaUtilService;

	private final String resourceRoot = "dic";//schema文件存放在resources下的根目录名
	private final String resourceSuffix = "dic";//schema文件后缀


	@Override
	public ValidateResult validateDicKey(String schemaCode, List<Map<String, Object>> data) {
		return validateDicKey(schemaCode, data,false);
	}

	@Override
	public ValidateResult validateDicKey(String schemaCode, Map<String, Object> data) {
		return validateDicKey(schemaCode,data,false);
	}

	@Override
	public ValidateResult validateDicKey(List<Map<String, Object>> schemaDataItems, List<Map<String, Object>> data) {
		return validateDicKey(schemaDataItems,data,false);
	}

	@Override
	public ValidateResult validateDicKey(String schemaCode, List<Map<String, Object>> data, boolean throwable) {
		List<Map<String, Object>> schemaDataItems = schemaUtilService.getSchemaDataItems(schemaCode);
		return validateDicKey(schemaDataItems, data,throwable);
	}

	@Override
	public ValidateResult validateDicKey(String schemaCode, Map<String, Object> data, boolean throwable) {
		List<Map<String, Object>> schemaDataItems = schemaUtilService.getSchemaDataItems(schemaCode);
		List<Map<String,Object>> list = new ArrayList<>();
		list.add(data);
		return validateDicKey(schemaDataItems,list,throwable);
	}


	@Override
	public ValidateResult validateDicKey(List<Map<String, Object>> schemaDataItems, List<Map<String, Object>> data, boolean throwable) {
		ValidateResult va = new ValidateResult();
		if (data != null) {
			for (int i = 0; i < schemaDataItems.size(); i++) {
				Map<String, Object> item = schemaDataItems.get(i);
				if (item.containsKey(SchemaConfigConstants.item_dic)) {
					Map<String, Object> dic = (Map<String, Object>) item.get(SchemaConfigConstants.item_dic);
					List<Map<String, Object>> dicData = getDicData(dic);
					for (int j = 0; j < data.size(); j++) {
						Map<String, Object> dataDetail = data.get(j);
						String itemId = VTools.StringIsEmpty(item.get(SchemaConfigConstants.item_alias) + "")
								?
								item.get(SchemaConfigConstants.item_id) + ""
								:
								item.get(SchemaConfigConstants.item_alias) + "";
						String values = dataDetail.get(itemId) + "";
						if (VTools.StringIsEmpty(values)) {
							if("true".equals(item.get(SchemaConfigConstants.item_notNull)+"")){
								va.addError("字典类型【"+item.get(SchemaConfigConstants.item_text)+":"+itemId+"】不能为空" );
							}
						} else {
							String[] varArr = values.split(",");
							for (String var : varArr) {
								Map<String, Object> dicRecord = getDicRecord(dicData, var);
								if (dicRecord == null) {
									va.addError("字典类型【"+item.get(SchemaConfigConstants.item_text)+":"+itemId+"】中key值【"+var+"】不匹配" );
								}
							}
						}
					}
				}
			}
		}
		if(throwable){
			throw new BusinessException(va.getStringErrors());
		}
		return va;
	}


	@Override
	public Map<String, Object> addDicText(Map req){
		if(!req.containsKey("schemaCode")){
			throw new BusinessException("schemaCode不能为空!");
		}
		try{
			String schemaCode = req.get("schemaCode")+"";
			List<Map<String, Object>> data = (List<Map<String, Object>>)req.get("data");
			addDicText(schemaCode,data);
			Map<String, Object> res = new HashMap<>();
			res.put("data",data);
			return res;
		}catch (Exception e){
			e.printStackTrace();
			throw new BusinessException(e.getMessage(),e);
		}
	}

	@Override
	public List<Map<String, Object>> addDicText(String schemaCode, List<Map<String, Object>> data) {
		List<Map<String, Object>> schemaDataItems = schemaUtilService.getSchemaDataItems(schemaCode);
		return addDicText(schemaDataItems, data);
	}

	@Override
	public Map<String, Object> addDicText(String schemaCode, Map<String, Object> data) {
		List<Map<String, Object>> schemaDataItems = schemaUtilService.getSchemaDataItems(schemaCode);
		List<Map<String,Object>> list = new ArrayList<>();
		list.add(data);
		return addDicText(schemaDataItems,list).get(0);
	}

	@Override
	public List<Map<String, Object>> addDicText(List<Map<String, Object>> schemaDataItems, List<Map<String, Object>> data) {
		if (data == null) {
			return data;
		}
		for (int i = 0; i < schemaDataItems.size(); i++) {
			Map<String, Object> item = schemaDataItems.get(i);
			if (item.containsKey(SchemaConfigConstants.item_dic)) {
				Map<String, Object> dic = (Map<String, Object>) item.get(SchemaConfigConstants.item_dic);
				List<Map<String, Object>> dicData;
				if(dic.containsKey(SchemaConfigConstants.item_dic_data)){
					dicData = (List<Map<String, Object>>) dic.get(SchemaConfigConstants.item_dic_data);
				}else {
					dicData = getDicData(dic);
				}
				for (int j = 0; j < data.size(); j++) {
					Map<String, Object> dataDetail = data.get(j);
					String itemId = VTools.StringIsEmpty(item.get(SchemaConfigConstants.item_alias) + "")
							?
							item.get(SchemaConfigConstants.item_id) + ""
							:
							item.get(SchemaConfigConstants.item_alias) + "";
					String values = dataDetail.get(itemId) + "";
					if (VTools.StringIsEmpty(values)) {
						dataDetail.put(itemId + "_" + DicDataConstants.text, null);
					} else {
						String text = "";
						String[] varArr = values.split(",");
						for (String var : varArr) {
							Map<String, Object> dicRecord = getDicRecord(dicData, var);
							if (dicRecord != null && dicRecord.containsKey(DicDataConstants.text)) {
								text += dicRecord.get(DicDataConstants.text) + ",";
							} else {
								text += dataDetail.get(itemId) + ",";
							}
						}
						if (text.length() > 0) {
							text = text.substring(0, text.length() - 1);
						}
						dataDetail.put(itemId + "_" + DicDataConstants.text, text);
					}
				}
			}
		}
		return data;
	}

	@Override
	public List<Map<String, Object>> getDicData(String dicCode) {
		Map<String, Object> schemaDicCfg = new HashMap<>();
		schemaDicCfg.put(SchemaConfigConstants.item_dic_id, dicCode);
		return getDicData(schemaDicCfg);
	}

	@Override
	public List<Map<String, Object>> getDicData(Map<String, Object> schemaDicCfg) {
		String dicCode = schemaDicCfg.get(SchemaConfigConstants.item_dic_id) + "";
		if (VTools.StringIsEmpty(dicCode)) {
			throw new BusinessException("dicCode不能为空");
		}
		Object dic = MemoryCache.getDicCache(dicCode);
		if (dic == null) {
			dic = getLocalDic(dicCode);
			MemoryCache.putDicCache(dicCode,dic);
		}
		if (dic != null) {
			if(List.class.isAssignableFrom(dic.getClass())){
				return (List<Map<String, Object>>) dic;
			}
			Map<String,Object> dicCfg = ((Map<String, Object>)dic);
			String type =  dicCfg.get(DicConfigConstants.type) + "";
			if (!DicConfigConstants.typeOfTree.equals(type)) {//标准字典
				return getSimpleDicData(dicCfg, schemaDicCfg);
			} else {//树形字典
				return getTreeDicData(dicCfg, schemaDicCfg);
			}
		}
		return null;
	}

	private Map<String, Object> getSimpleDicData(List<Map<String,Object>> dicData){
		Map<String, Object> result = new HashMap<>();
		for (Map<String, Object> r : dicData) {
			result.put(r.get(DicDataConstants.key) + "", r.get(DicDataConstants.text));
		}
		return result;
	}
	@Override
	public Map<String, Object> getSimpleDicData(String dicCode) {
		List<Map<String, Object>> dicData = getDicData(dicCode);
		return getSimpleDicData(dicData);
	}

	@Override
	public Map<String, Object> getSimpleDicData(Map<String, Object> schemaDicCfg) {
		List<Map<String, Object>> dicData = getDicData(schemaDicCfg);
		return getSimpleDicData(dicData);
	}

	private Map<String, Object> getDicRecord(List<Map<String, Object>> dicData, String key) {
		for (Map<String, Object> record : dicData) {
			if ((record.get(DicDataConstants.key) + "").equals(key)) {
				return record;
			} else {
				List<Map<String, Object>> children = ((List<Map<String, Object>>) record.get(DicDataConstants.children));
				if (children != null && children.size() > 0) {//递归调用遍历子节点
					Map<String, Object> recordFlag = getDicRecord(children, key);
					if (recordFlag == null) {
						continue;
					} else {
						return recordFlag;
					}
				} else {
					continue;
				}
			}
		}
		return null;
	}

	@Override
	public Map<String, Object> getDicRecord(String dicCode, String key) {
		List<Map<String, Object>> dicData = getDicData(dicCode);
		return getDicRecord(dicData, key);
	}

	@Override
	public Map<String, Object> getDicRecord(Map<String, Object> schemaDicCfg, String key) {
		List<Map<String, Object>> dicData = getDicData(schemaDicCfg);
		return getDicRecord(dicData, key);
	}

	@Override
	public void deleteDicByTableName(String tableName) {
		MemoryCache.removeDicCache(tableName.toLowerCase());
		MemoryCache.removeDicDataCache(tableName.toLowerCase());
	}

	@Override
	public Object getLocalDic(String dicCode) {
		if (VTools.StringIsEmpty(dicCode)) {
			throw new BusinessException("dicCode不能为空");
		}
		try {
			ResourceDTO resourceDTO = VTools.getResource(resourceRoot + "/" + dicCode + "." + resourceSuffix);
			if(resourceDTO == null){
				throw new BusinessException("未找到" + dicCode + ",对应的字典配置!");
			}
			StringBuilder sb = new StringBuilder();
			String line;

			BufferedReader br = new BufferedReader(new InputStreamReader(resourceDTO.getInputStream(), "UTF-8"));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String jsonStr = sb.toString();
			if (!VTools.StringIsEmpty(jsonStr)) {
				if(jsonStr.trim().indexOf("[") == 0){
					return GsonUtils.fromJson(jsonStr, List.class);
				}else {
					return GsonUtils.fromJson(jsonStr, Map.class);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	//以下是私有方法
//	/**
//	 * 标准字典数据获取方法
//	 * @param dicCode
//	 * @param sysDic
//	 * @param dicCfg
//	 * @return
//	 */
//	private List<Map<String,Object>> getSimpleDicData(String dicCode,SysDic sysDic,
//															 Map<String,Object> dicCfg){
//		StringBuilder dicWhere = new StringBuilder();
//		if(dicCfg.containsKey(SchemaConfigConstants.item_dic_cnd)) {
//			List<Object> cnd = (List<Object>)dicCfg.get(SchemaConfigConstants.item_dic_cnd);
//			dicWhere.append(CndAnalysis.instance().toString(cnd));
//		}
//		List<Map<String,Object>> result = null;
//		int subOrg = sysDic.getIS_ORG();
//		if(CommonConstants.YES == subOrg){
//			String orgCode = SecurityUtils.getUserInfo().getSysOrg().getORG_CODE();
//			String cacheName = buildDicDataCacheName(dicCode,orgCode,dicWhere.toString());
//			result = (List<Map<String, Object>>)redisTemplate.opsForValue().get(cacheName);
//			if(result==null){
//				result = new ArrayList<>();
//				Map<String,Object> OrgParameters = new HashMap<>();
//				OrgParameters.put("orgCode",orgCode);
//				OrgParameters.put("dicCode",dicCode);
//				StringBuilder sqlDicDeOrg = new StringBuilder("select * from SYS_DIC_DE_ORG where DIC_CODE = :dicCode and ORG_CODE = :orgCode");
//				if(dicWhere.length()>0){
//					sqlDicDeOrg.append(" and "+dicWhere);
//				}
//				sqlDicDeOrg.append(" order by SORT,CODE");
//				List<SysDicDe> sysDicDes = dao.doSqlQuery(sqlDicDeOrg.toString(),SysDicDe.class, OrgParameters);
//				if(sysDicDes == null || sysDicDes.size() == 0){
//					Map<String,Object> parameters = new HashMap<>();
//					parameters.put("dicCode",dicCode);
//					StringBuilder sqlDicDe = new StringBuilder("select * from SYS_DIC_DE where DIC_CODE = :dicCode");
//					if(dicWhere.length()>0){
//						sqlDicDe.append(" and "+dicWhere);
//					}
//					sqlDicDe.append(" order by SORT,CODE");
//					sysDicDes  = dao.doSqlQuery(sqlDicDe.toString(), SysDicDe.class,parameters);
//				}
//				if(sysDicDes != null && sysDicDes.size()>0){
//					for(SysDicDe data:sysDicDes){
//						Map<String,Object> dic = new HashMap<>();
//						dic.put(DicDataConstants.key,data.getCODE());
//						dic.put(DicDataConstants.text,data.getNAME());
//						dic.put(DicDataConstants.queryValues,data.getCODE()+","+data.getNAME()+","+data.getPY()+","+data.getWB());
//						dic.put(DicDataConstants.props,data);
//						result.add(dic);
//					}
//				}
//				redisTemplate.opsForValue().set(cacheName,result);
//			}
//		}else {
//			String cacheName = buildDicDataCacheName(dicCode,dicWhere.toString());
//			result = (List<Map<String, Object>>) redisTemplate.opsForValue().get(cacheName);
//			if (result == null) {
//				result = new ArrayList<>();
//				Map<String, Object> parameters = new HashMap<>();
//				parameters.put("dicCode", dicCode);
//				StringBuilder sqlDicDeOrg = new StringBuilder("select * from SYS_DIC_DE where DIC_CODE = :dicCode");
//				if (dicWhere.length() > 0) {
//					sqlDicDeOrg.append(" and " + dicWhere);
//				}
//				sqlDicDeOrg.append(" order by SORT,CODE");
//				List<SysDicDe> sysDicDes = dao.doSqlQuery(sqlDicDeOrg.toString(),SysDicDe.class, parameters);
//				if (sysDicDes != null && sysDicDes.size() > 0) {
//					for(SysDicDe data:sysDicDes){
//						Map<String,Object> dic = new HashMap<>();
//						dic.put(DicDataConstants.key,data.getCODE());
//						dic.put(DicDataConstants.text,data.getNAME());
//						dic.put(DicDataConstants.queryValues,data.getCODE()+","+data.getNAME()+","+data.getPY()+","+data.getWB());
//						dic.put(DicDataConstants.props,data);
//						result.add(dic);
//					}
//				}
//				redisTemplate.opsForValue().set(cacheName, result);
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * 标准字典树数据获取方法
//	 * @param dicCode
//	 * @param sysDic
//	 * @param dicCfg
//	 * @return
//	 */
//	private List<Map<String,Object>> getSimpleDicTreeData(String dicCode,SysDic sysDic,
//														  Map<String,Object> dicCfg){
//		StringBuilder dicWhere = new StringBuilder();
//		if(dicCfg.containsKey(SchemaConfigConstants.item_dic_cnd)) {
//			List<Object> cnd = (List<Object>)dicCfg.get(SchemaConfigConstants.item_dic_cnd);
//			dicWhere.append(CndAnalysis.instance().toString(cnd));
//		}
//		/**
//		 * 字典配置
//		 */
//		String checkable = dicCfg.get(SchemaConfigConstants.item_dic_checkable) + "";
//		List<Map<String,Object>> result = null;
//		int subOrg = sysDic.getIS_ORG();
//		if(CommonConstants.YES == subOrg){
//			String orgCode = SecurityUtils.getUserInfo().getSysOrg().getORG_CODE();
//			String cacheName = buildDicDataCacheName(dicCode,orgCode,dicWhere.toString()
//				,SchemaConfigConstants.item_dic_checkable+"="+checkable.equals("true"));
//			result = (List<Map<String, Object>>)redisTemplate.opsForValue().get(cacheName);
//			if(result==null){
//				result = new ArrayList<>();
//				Map<String,Object> OrgParameters = new HashMap<>();
//				OrgParameters.put("orgCode",orgCode);
//				OrgParameters.put("dicCode",dicCode);
//				StringBuilder sqlDicDeOrg = new StringBuilder("select * from SYS_DIC_DE_ORG where DIC_CODE = :dicCode and ORG_CODE = :orgCode");
//				if(dicWhere.length()>0){
//					sqlDicDeOrg.append(" and "+dicWhere);
//				}
//				sqlDicDeOrg.append(" order by SORT,CODE");
//				List<SysDicDe> sysDicDes = dao.doSqlQuery(sqlDicDeOrg.toString(), SysDicDe.class,OrgParameters);
//				if(sysDicDes == null || sysDicDes.size() == 0){
//					Map<String,Object> parameters = new HashMap<>();
//					parameters.put("dicCode",dicCode);
//					StringBuilder sqlDicDe = new StringBuilder("select * from SYS_DIC_DE where DIC_CODE = :dicCode");
//					if(dicWhere.length()>0){
//						sqlDicDe.append(" and "+dicWhere);
//					}
//					sqlDicDe.append(" order by SORT,CODE");
//					sysDicDes  = dao.doSqlQuery(sqlDicDe.toString(),SysDicDe.class, parameters);
//				}
//				if(sysDicDes != null && sysDicDes.size()>0){
//					for(SysDicDe data:sysDicDes){
//						Map<String, Object> dicTree = new HashMap<>();
//						dicTree.put(DicDataConstants.key,data.getCODE());
//						dicTree.put(DicDataConstants.text,data.getNAME());
//						dicTree.put(DicDataConstants.parentKey,data.getPARENT_CODE());
//						dicTree.put(DicDataConstants.props,data);
//						result.add(dicTree);
//					}
//					result = TreeConverter.instance()
//						.setCheckable(checkable.equals("true"))
//						.getExtTreeList(result, DicDataConstants.key, DicDataConstants.parentKey);
//				}
//				redisTemplate.opsForValue().set(cacheName, result);
//			}
//		}else {
//			String cacheName = buildDicDataCacheName(dicCode,dicWhere.toString()
//				,SchemaConfigConstants.item_dic_checkable+"="+checkable.equals("true"));
//			result = (List<Map<String, Object>>) redisTemplate.opsForValue().get(cacheName);
//			if (result == null) {
//				result = new ArrayList<>();
//				Map<String, Object> parameters = new HashMap<>();
//				parameters.put("dicCode", dicCode);
//				StringBuilder sqlDicDeOrg = new StringBuilder("select * from SYS_DIC_DE where DIC_CODE = :dicCode");
//				if (dicWhere.length() > 0) {
//					sqlDicDeOrg.append(" and " + dicWhere);
//				}
//				sqlDicDeOrg.append(" order by SORT,CODE");
//				List<SysDicDe> sysDicDes = dao.doSqlQuery(sqlDicDeOrg.toString(),SysDicDe.class, parameters);
//				if (sysDicDes != null && sysDicDes.size() > 0) {
//					for(SysDicDe data:sysDicDes){
//						Map<String, Object> dicTree = new HashMap<>();
//						dicTree.put(DicDataConstants.key,data.getCODE());
//						dicTree.put(DicDataConstants.text,data.getNAME());
//						dicTree.put(DicDataConstants.parentKey,data.getPARENT_CODE());
//						dicTree.put(DicDataConstants.props,data);
//						result.add(dicTree);
//					}
//					result = TreeConverter.instance()
//						.setCheckable(checkable.equals("true"))
//						.getExtTreeList(result, DicDataConstants.key, DicDataConstants.parentKey);
//				}
//				redisTemplate.opsForValue().set(cacheName, result);
//			}
//		}
//		return result;
//	}

	/**
	 * 标准字典数据获取方法
	 *
	 * @param dicCfg
	 * @return
	 */
	private List<Map<String, Object>> getSimpleDicData(Map<String, Object> dicCfg, Map<String, Object> schemaDicCfg) {

		String dicCode = schemaDicCfg.get(SchemaConfigConstants.item_dic_id) + "";
		if (VTools.StringIsEmpty(dicCode)) {
			throw new BusinessException("dicCode不能为空");
		}
		StringBuilder dicWhere = new StringBuilder();
		if (schemaDicCfg.containsKey(SchemaConfigConstants.item_dic_cnd)) {
			List<Object> cnd = (List<Object>) schemaDicCfg.get(SchemaConfigConstants.item_dic_cnd);
			dicWhere.append(CndAnalysis.instance().toString(cnd));
		}
		List<Map<String, Object>> result = null;
		String queryOnlyStr = dicCfg.get(DicConfigConstants.queryOnly) + "";
		boolean queryOnly = VTools.StringIsEmpty(queryOnlyStr) ? false : ("true".equals(queryOnlyStr) ? true : false);
		if (!dicCfg.containsKey(DicConfigConstants.tableName)) {
			throw new BusinessException("没有找到字典:[" + dicCode + "]的tableName");
		}
		String tableName = (dicCfg.get(DicConfigConstants.tableName) + "");
		if (dicCfg.containsKey(DicConfigConstants.cnd) && null != dicCfg.get(DicConfigConstants.cnd)) {
			List<Object> cnd = (List<Object>) dicCfg.get(DicConfigConstants.cnd);
			if (dicWhere.length() > 0) {
				dicWhere.append(" and ");
			}
			dicWhere.append(CndAnalysis.instance().toString(cnd));
		}
		String cacheName = buildDicDataCacheName(tableName.toLowerCase(), dicCode, dicWhere.toString());
//		if("SYS_DIC".equals(tableName.toUpperCase())){//sys_dic表默认添加dic_code=dicCode条件
//			dicWhere.append(dicWhere.length() > 0 ? " and " : "").append("a.DIC_CODE='"+dicCode+"'");
//		}
		if (!queryOnly) {
			result = (List<Map<String, Object>>) MemoryCache.getDicDataCache(cacheName);
		}
		if (result == null) {
			result = new ArrayList<>();

			if (!dicCfg.containsKey(DicConfigConstants.key)) {
				throw new BusinessException("没有找到字典:[" + dicCode + "]的key");
			}
			if (!dicCfg.containsKey(DicConfigConstants.text)) {
				throw new BusinessException("没有找到字典:[" + dicCode + "]的text");
			}
			/**
			 * 字典配置
			 */
			Map<String, String> keyItems = (Map<String, String>) dicCfg.get(DicConfigConstants.key);
			Map<String, String> textItems = (Map<String, String>) dicCfg.get(DicConfigConstants.text);
			List<Map<String, String>> queryProperties = (List<Map<String, String>>) dicCfg.get(DicConfigConstants.queryProps);
			List<Map<String, String>> properties = (List<Map<String, String>>) dicCfg.get(DicConfigConstants.props);
			if (queryProperties == null) {
				queryProperties = new ArrayList<>();
			}
			if (properties == null) {
				properties = new ArrayList<>();
			}
			/**
			 * key,text和queryProps中的字段放入properties中 start
			 */
			if (!keyItems.containsKey(DicConfigConstants.key_id)) {
				throw new BusinessException("没有找到字典:[" + dicCode + "]key中的id");
			}
			if (!textItems.containsKey(DicConfigConstants.text_id)) {
				throw new BusinessException("没有找到字典:[" + dicCode + "]text中的id");
			}
			if (!keyItems.containsKey(DicConfigConstants.key_alias)) {//默认id作为alias
				keyItems.put(DicConfigConstants.key_alias, keyItems.get(DicConfigConstants.key_id));
			}
			if (!textItems.containsKey(DicConfigConstants.text_alias)) {//默认id作为alias
				textItems.put(DicConfigConstants.text_alias, textItems.get(DicConfigConstants.text_id));
			}
			for (Map<String, String> prop : properties) {
				if (!prop.containsKey(DicConfigConstants.props_id)) {
					throw new BusinessException("没有找到字典:[" + dicCode + "]props中的id");
				}
				if (!prop.containsKey(DicConfigConstants.props_alias)) {
					prop.put(DicConfigConstants.props_alias, prop.get(DicConfigConstants.props_id));
				}
			}
			for (Map<String, String> queryProp : queryProperties) {
				if (!queryProp.containsKey(DicConfigConstants.queryProps_id)) {
					throw new BusinessException("没有找到字典:[" + dicCode + "]queryProps中的id");
				}
				if (!queryProp.containsKey(DicConfigConstants.queryProps_alias)) {
					queryProp.put(DicConfigConstants.queryProps_alias, queryProp.get(DicConfigConstants.queryProps_id));
				}
			}
			queryProperties.add(keyItems);
			queryProperties.add(textItems);
			queryProperties = buildDicProperties(queryProperties);
			properties.addAll(queryProperties);
			properties = buildDicProperties(properties);
			/**
			 * key,text和queryProps中的字段放入properties中 end
			 */

			//组装sql
			StringBuilder where = new StringBuilder();
			if(dicWhere.length() > 0){
				where.append(" where ").append(dicWhere);
			}
			String dicSql = buildSql(dicCfg,properties,where);

			List<Map<String, Object>> list = dao.doSqlQuery(dicSql, null);
//				DicUtils.getDicByCode(items,data,redisTemplate,dao);
//				res.put("data",data);
			for (Map<String, Object> data : list) {
				Map<String, Object> dic = new HashMap<>();
				dic.put(DicDataConstants.key, VTools.toString(data.get(keyItems.get(DicConfigConstants.key_alias))));
				dic.put(DicDataConstants.text, VTools.toString(data.get(textItems.get(DicConfigConstants.text_alias))));
				List<String> queryValues = new ArrayList<>();
				for (Map<String, String> queryProp : queryProperties) {
					String queryValue = VTools.toString(data.get(queryProp.get(DicConfigConstants.queryProps_alias)));
					if(queryValue != null
						&& !queryValues.contains(queryValue)){
						queryValues.add(queryValue);
					}
				}
				dic.put(DicDataConstants.queryValues, String.join(",", queryValues));
				Map<String, String> propsMap = new HashMap<>();
				if (!VTools.ListIsEmpty(properties)) {
					for (Map<String, String> prop : properties) {
						propsMap.put(prop.get(DicConfigConstants.props_alias), VTools.toString(data.get(prop.get(DicConfigConstants.props_alias))));
					}
					dic.put(DicDataConstants.props, propsMap);
				}
				result.add(dic);
			}
			if (!queryOnly) {
				MemoryCache.putDicDataCache(cacheName,result);
			}
		}
		return result;
	}

	/**
	 * 字典树数据获取方法
	 *
	 * @param dicCfg
	 * @return
	 */
	private List<Map<String, Object>> getTreeDicData(Map<String, Object> dicCfg, Map<String, Object> schemaDicCfg) {

		String dicCode = schemaDicCfg.get(SchemaConfigConstants.item_dic_id) + "";
		if (VTools.StringIsEmpty(dicCode)) {
			throw new BusinessException("dicCode不能为空");
		}
		StringBuilder dicWhere = new StringBuilder();
		if (schemaDicCfg.containsKey(SchemaConfigConstants.item_dic_cnd)) {
			List<Object> cnd = (List<Object>) schemaDicCfg.get(SchemaConfigConstants.item_dic_cnd);
			dicWhere.append(CndAnalysis.instance().toString(cnd));
		}

		List<Map<String, Object>> result = null;
		String queryOnlyStr = dicCfg.get(DicConfigConstants.queryOnly) + "";
		boolean queryOnly = VTools.StringIsEmpty(queryOnlyStr) ? false : ("true".equals(queryOnlyStr) ? true : false);
		if (!dicCfg.containsKey(DicConfigConstants.tableName)) {
			throw new BusinessException("没有找到字典:[" + dicCode + "]的tableName");
		}
		String tableName = (dicCfg.get(DicConfigConstants.tableName) + "");
		String checkable = dicCfg.get(DicConfigConstants.checkable) + "";
		//schema中的配置覆盖dicCfg中的配置
		if (schemaDicCfg.containsKey(SchemaConfigConstants.item_dic_checkable)) {
			checkable = schemaDicCfg.get(SchemaConfigConstants.item_dic_checkable) + "";
		}

		if (dicCfg.containsKey(DicConfigConstants.cnd) && null != dicCfg.get(DicConfigConstants.cnd)) {
			List<Object> cnd = (List<Object>) dicCfg.get(DicConfigConstants.cnd);
			if (dicWhere.length() > 0) {
				dicWhere.append(" and ");
			}
			dicWhere.append(CndAnalysis.instance().toString(cnd));
		}
		StringBuilder parentDicWhere = new StringBuilder();
		if (dicCfg.containsKey(DicConfigConstants.parentCnd) && null != dicCfg.get(DicConfigConstants.parentCnd)) {
			List<Object> parentCnd = (List<Object>) dicCfg.get(DicConfigConstants.parentCnd);
			parentDicWhere.append(CndAnalysis.instance().toString(parentCnd));
		}
		String cacheName = buildDicDataCacheName(tableName.toLowerCase(), dicCode, dicWhere.toString(), parentDicWhere.toString()
			, SchemaConfigConstants.item_dic_checkable + "=" + checkable.equals("true"));
//		if("SYS_DIC".equals(tableName.toUpperCase())){//sys_dic表默认添加dic_code=dicCode条件
//			dicWhere.append(dicWhere.length() > 0 ? " and " : "").append("a.DIC_CODE='"+dicCode+"'");
//		}
		if (!queryOnly) {
			result = (List<Map<String, Object>>) MemoryCache.getDicDataCache(cacheName);
		}
		if (result == null) {
			result = new ArrayList<>();
			if (!dicCfg.containsKey(DicConfigConstants.key)) {
				throw new BusinessException("没有找到字典:[" + dicCode + "]的key");
			}
			if (!dicCfg.containsKey(DicConfigConstants.text)) {
				throw new BusinessException("没有找到字典:[" + dicCode + "]的text");
			}
//			if (!dic_json_map.containsKey(DicConfigConstants.parentCnd)) {
//				throw new BusinessException("没有找到字典:[" + sysDic.getNAME() + "]的parentCnd");
//			}
			if (!dicCfg.containsKey(DicConfigConstants.parentKey)) {
				throw new BusinessException("没有找到字典:[" + dicCode + "]parentKey");
			}
			/**
			 * 字典配置
			 */
			Map<String, String> keyItems = (Map<String, String>) dicCfg.get(DicConfigConstants.key);
			Map<String, String> textItems = (Map<String, String>) dicCfg.get(DicConfigConstants.text);
			Map<String, String> parentKeyItems = (Map<String, String>) dicCfg.get(DicConfigConstants.parentKey);
			List<Map<String, String>> queryProperties = (List<Map<String, String>>) dicCfg.get(DicConfigConstants.queryProps);
			List<Map<String, String>> properties = (List<Map<String, String>>) dicCfg.get(DicConfigConstants.props);
			if (queryProperties == null) {
				queryProperties = new ArrayList<>();
			}
			if (properties == null) {
				properties = new ArrayList<>();
			}
			String parentIconCls = dicCfg.get(DicConfigConstants.parentIconCls) + "";
			String leafIconCls = dicCfg.get(DicConfigConstants.leafIconCls) + "";
			String expanded = dicCfg.get(DicConfigConstants.expanded) + "";
			/**
			 * key,text,parentKey和queryProps放入properties中 start
			 */
			if (!keyItems.containsKey(DicConfigConstants.key_id)) {
				throw new BusinessException("没有找到字典:[" + dicCode + "]key中的id");
			}
			if (!textItems.containsKey(DicConfigConstants.text_id)) {
				throw new BusinessException("没有找到字典:[" + dicCode + "]text中的id");
			}
			if (!parentKeyItems.containsKey(DicConfigConstants.parentKey_id)) {
				throw new BusinessException("没有找到字典:[" + dicCode + "]parentKey中的id");
			}
			if (!keyItems.containsKey(DicConfigConstants.key_alias)) {//默认id作为alias
				keyItems.put(DicConfigConstants.key_alias, keyItems.get(DicConfigConstants.key_id));
			}
			if (!textItems.containsKey(DicConfigConstants.text_alias)) {//默认id作为alias
				textItems.put(DicConfigConstants.text_alias, textItems.get(DicConfigConstants.text_id));
			}
			if (!parentKeyItems.containsKey(DicConfigConstants.parentKey_alias)) {//默认id作为alias
				parentKeyItems.put(DicConfigConstants.parentKey_alias, parentKeyItems.get(DicConfigConstants.parentKey_id));
			}
			for (Map<String, String> prop : properties) {
				if (!prop.containsKey(DicConfigConstants.props_id)) {
					throw new BusinessException("没有找到字典:[" + dicCode + "]props中的id");
				}
				if (!prop.containsKey(DicConfigConstants.props_alias)) {
					prop.put(DicConfigConstants.props_alias, prop.get(DicConfigConstants.props_id));
				}
			}
			for (Map<String, String> queryProp : queryProperties) {
				if (!queryProp.containsKey(DicConfigConstants.queryProps_id)) {
					throw new BusinessException("没有找到字典:[" + dicCode + "]queryProps中的id");
				}
				if (!queryProp.containsKey(DicConfigConstants.queryProps_alias)) {
					queryProp.put(DicConfigConstants.queryProps_alias, queryProp.get(DicConfigConstants.queryProps_id));
				}
			}
			queryProperties.add(keyItems);
			queryProperties.add(textItems);
			queryProperties = buildDicProperties(queryProperties);
			properties.addAll(queryProperties);
			properties.add(parentKeyItems);
			properties = buildDicProperties(properties);
			/**
			 * key,text,parentKey和queryProps中的字段放入properties中 end
			 */
			//组装sql
			StringBuilder where = new StringBuilder();
			StringBuilder parentWhere = new StringBuilder();
			if(dicWhere.length() > 0){
				where.append(" where ").append(dicWhere);
				parentWhere.append(" where ").append(dicWhere);
			}
			if (parentDicWhere.length() > 0) {
				parentWhere.append(parentWhere.length()>0?" and " : " where ").append(parentDicWhere);
			}
			String parentSql = buildSql(dicCfg,properties,parentWhere);
			//先查父节点
			List<Map<String, Object>> parentList = dao.doSqlQuery(parentSql, null);
			if (!VTools.ListIsEmpty(parentList)) {
				List<String> startKeys = new ArrayList<>();
				List<String> propKeys = new ArrayList<>();
				for(Map<String,String> prop :properties){
					propKeys.add(prop.get(DicConfigConstants.props_alias));
				}
				List<String> queryValueKeys = new ArrayList<>();
				for (Map<String, String> queryProp : queryProperties) {
					queryValueKeys.add(queryProp.get(DicConfigConstants.queryProps_alias));
				}
				/**
				 * 如果parentCnd没有配置，则以cnd条件查询一次数据库并组装成树结构
				 */
				if (!dicCfg.containsKey(DicConfigConstants.parentCnd)) {
					//生成树结构
					result = TreeConverter.instance()
						.setParentIconCls(VTools.StringIsEmpty(parentIconCls) ? null : parentIconCls)
						.setLeafIconCls(VTools.StringIsEmpty(leafIconCls) ? null : leafIconCls)
						.setCheckable("true".equals(checkable))
						.setExpandAll(expanded.equals("true"))
						.setKeyName(keyItems.get(DicConfigConstants.key_alias))
						.setParentKeyName(parentKeyItems.get(DicConfigConstants.parentKey_alias))
						.setTextKeyName(textItems.get(DicConfigConstants.text_alias))
						.setPropKeys(propKeys)
						.setQueryValueKeys(queryValueKeys)
						.setConverterType(TreeConverter.TYPE_DIC)
						.getTree(parentList);
					if (!queryOnly) {
						MemoryCache.putDicDataCache(cacheName,result);
					}
					return result;
				}
				for (Map<String, Object> parent : parentList) {
					startKeys.add(VTools.toString(parent.get(keyItems.get(DicConfigConstants.key_alias))));
				}
				//查所有节点
				String sql = buildSql(dicCfg,properties,where);
				List<Map<String, Object>> list = dao.doSqlQuery(sql, null);
				//生成树结构
				result = TreeConverter.instance()
					.setParentIconCls(VTools.StringIsEmpty(parentIconCls) ? null : parentIconCls)
					.setLeafIconCls(VTools.StringIsEmpty(leafIconCls) ? null : leafIconCls)
					.setCheckable("true".equals(checkable))
					.setExpandAll(expanded.equals("true"))
					.setKeyName(keyItems.get(DicConfigConstants.key_alias))
					.setParentKeyName(parentKeyItems.get(DicConfigConstants.parentKey_alias))
					.setTextKeyName(textItems.get(DicConfigConstants.text_alias))
					.setPropKeys(propKeys)
					.setQueryValueKeys(queryValueKeys)
					.setConverterType(TreeConverter.TYPE_DIC)
					.getTree(list, startKeys);
			}
			if (!queryOnly) {
				MemoryCache.putDicDataCache(cacheName,result);
			}
		}
		return result;
	}

	/**
	 * 组装sql
	 */
	private String buildSql(Map<String,Object> dicCfg,List<Map<String,String>> properties,StringBuilder dicWhere){
		/**
		 * 多表关联
		 * relations 配置说明 entityName joinMethod parent cnd
		 * tableName不能为空
		 * 默认cross交叉连接 joinMethod可配置 left right cross
		 */
		String tableName = (dicCfg.get(DicConfigConstants.tableName) + "");
		String sort = dicCfg.get(DicConfigConstants.sort) + "";
		int tableNameAliasAscii = 97;//table别名 ascii码表示 97=a
		List<Map<String, Object>> crossJoinTables = new ArrayList<>();//交叉连接表列表
		List<Map<String, Object>> otherJoinTables = new ArrayList<>();//其他连接表列表
		Map<String, Object> mainTable = new HashMap<>();
		mainTable.put(DicConfigConstants.tableName, tableName);
		mainTable.put(DicConfigConstants.relations_parent, null);//关联的父表 null为主表
		mainTable.put(DicConfigConstants.key_tableAlias, ((char) tableNameAliasAscii + ""));//表别名
		tableNameAliasAscii++;

		crossJoinTables.add(mainTable);

		List<Map<String, Object>> relations = (List<Map<String, Object>>) dicCfg.get(DicConfigConstants.relations);
		if (!VTools.ListIsEmpty(relations)) {
			for (int i=0;i<relations.size();i++) {
				Map<String, Object> relation = relations.get(i);
				if (!relation.containsKey(DicConfigConstants.relations_tableName)) {
					throw new BusinessException("关联表" + (char) tableNameAliasAscii + "tableName不能为空!");
				}
				Map<String, Object> relationTable = new HashMap<>();
				String relationTableName = relation.get(DicConfigConstants.relations_tableName) + "";
				String joinMethod = relation.containsKey(DicConfigConstants.relations_joinMethod) ?
					relation.get(DicConfigConstants.relations_joinMethod) + ""
					: "cross";//默认交叉连接
				String tableAlias = (char)tableNameAliasAscii+"";
				tableNameAliasAscii++;
				relation.put(DicConfigConstants.key_tableAlias,tableAlias);
				//关联条件
				if (relation.containsKey(DicConfigConstants.relations_cnd)) {
					List<Object> joinCnd = (List<Object>) relation.get("cnd");
					relationTable.put(DicConfigConstants.relations_cnd, joinCnd);
				}
				//放入实体类名
				relationTable.put(DicConfigConstants.relations_tableName, relationTableName);
				//放入关联类型
				relationTable.put(DicConfigConstants.relations_joinMethod, joinMethod);
				//放入表别名
				relationTable.put(DicConfigConstants.key_tableAlias, tableAlias);

				String parent = "";
				if ("cross".equals(joinMethod)) {
					//放入关联的父表
					relationTable.put(DicConfigConstants.relations_parent,null);
					crossJoinTables.add(relationTable);
				} else {

					if(relation.containsKey(DicConfigConstants.relations_parent)){
						parent = relation.get(DicConfigConstants.relations_parent)+"";
					}else {
						if(i == 0){
							parent = mainTable.get(DicConfigConstants.key_tableAlias)+"";
						}else {
							parent = relations.get(i-1).get(DicConfigConstants.key_tableAlias)+"";//默认关联上一张表
						}
					}
					//放入关联的父表
					relationTable.put(DicConfigConstants.relations_parent,parent);
					otherJoinTables.add(relationTable);
				}
			}
		}
		if(!VTools.ListIsEmpty(otherJoinTables)){
			otherJoinTables = TreeConverter.instance()
				.setConverterType(TreeConverter.TYPE_DEFAULT)
				.setKeyName(DicConfigConstants.key_tableAlias)
				.setParentKeyName(DicConfigConstants.relations_parent)
				.getTree(otherJoinTables);
		}

		//组装sql
		StringBuilder dicSql = new StringBuilder("select ");
		for (int i = 0; i < properties.size(); i++) {
			Map<String, String> prop = properties.get(i);
			if(i!=0){
				dicSql.append(",");
			}
			String propId = prop.get(DicConfigConstants.props_id);
			String prop_tableAlias = prop.get(DicConfigConstants.props_tableAlias);
			if (!VTools.StringIsEmpty(prop_tableAlias)) {
				propId = prop_tableAlias + "." + propId;
			} else {//默认查询主表的字段
				propId = mainTable.get(DicConfigConstants.props_tableAlias) + "." + propId;
			}
			if(prop.containsKey(DicConfigConstants.props_type)){
				String type = prop.get(DicConfigConstants.props_type);
				switch (type){
					case "date":
						dicSql.append(DBFunctionUtils.DATE_FORMAT(propId,"yyyy-MM-dd"));
						break;
					case "datetime":
						dicSql.append(DBFunctionUtils.DATE_FORMAT(propId,"yyyy-MM-dd HH:mm:ss"));
						break;
					case "boolean":
						dicSql.append("case ").append(propId).append(" when 0 then false when null then false when 1 then true else ").append(propId).append(" end");
						break;
					default:
						dicSql.append(propId);
				}
			}else {
				dicSql.append(propId);
			}
			dicSql.append(" as ");
			String prop_alias = prop.get(DicConfigConstants.props_alias);
			if(!VTools.StringIsEmpty(prop_alias)){//查询字段的别名
				dicSql.append("\"").append(prop_alias).append("\"");
			}else {
				dicSql.append("\"").append(prop.get(DicConfigConstants.props_id)).append("\"");
			}
		}

		//组装from 表关联
		StringBuilder from = new StringBuilder();
		for (int i = 0; i < crossJoinTables.size(); i++) {
			Map<String, Object> crossJoinTable = crossJoinTables.get(i);
			if (i == 0) {//主表
				from.append(" from ");
			}
			from.append(crossJoinTable.get(DicConfigConstants.relations_tableName))
				.append(" ")
				.append(crossJoinTable.get(DicConfigConstants.key_tableAlias));
			//组装当前表的其他关联表
			buildOtherJoinFromSQL(crossJoinTable,from,otherJoinTables);
			if (i < crossJoinTables.size() - 1) {
				from.append(",");
			}
			//交叉连接的cnd 条件放在where 里
			if (crossJoinTable.containsKey(DicConfigConstants.relations_cnd)) {
				List<Object> cnd = (List<Object>) crossJoinTable.get(DicConfigConstants.relations_cnd);
				if (cnd != null && cnd.size() > 0) {//组装where条件
					buildWhereSql(dicWhere, CndAnalysis.instance().toString(cnd));
				}
			}
		}
		dicSql.append(from);
		dicSql.append(dicWhere);
		if (!VTools.StringIsEmpty(sort)) {
			dicSql.append(" order by ").append(sort);
		}
		return dicSql.toString();
	}

	private void buildOtherJoinFromSQL(Map<String,Object> parentTable,StringBuilder from,List<Map<String,Object>> otherJoinTables){
		for(Map<String,Object> otherTable:otherJoinTables) {
			if ((otherTable.get(DicConfigConstants.relations_parent)).equals(parentTable.get(DicConfigConstants.key_tableAlias))) {
				String joinMethod = otherTable.get(DicConfigConstants.relations_joinMethod)+"";
				//组装join 连接sql
				from.append(" ")
					.append(joinMethod.toLowerCase().trim())
					.append(" join ")
					.append(otherTable.get(DicConfigConstants.relations_tableName))
					.append(" ")
					.append(otherTable.get(DicConfigConstants.key_tableAlias));
				if(otherTable.containsKey(DicConfigConstants.relations_cnd)){
					List<Object> relation_cnd = (List<Object>) otherTable.get(DicConfigConstants.relations_cnd);
					if(relation_cnd!= null && relation_cnd.size() > 0){
						from.append(" on ")
							.append(CndAnalysis.instance().toString(relation_cnd));
					}else {
						throw new BusinessException("relation table["+otherTable.get(DicConfigConstants.relations_tableName)+"] has empty cnd");
					}
				}else {
					throw new BusinessException("relation table["+otherTable.get(DicConfigConstants.relations_tableName)+"] has empty cnd");
				}
				if(otherTable.containsKey(DicDataConstants.children)){
					List<Map<String,Object>> children = (List) otherTable.get(DicDataConstants.children);
					if(!VTools.ListIsEmpty(children)){
						buildOtherJoinFromSQL(otherTable,from,children);
					}
				}
			}
		}
	}

	/**
	 * 字典配置缓存命名生成
	 *
	 * @param name
	 * @return
	 */
	private String buildDicCacheName(String name) {
		String cacheName = name;
		return cacheName;
	}

	/**
	 * 字典数据缓存命名生成
	 *
	 * @param tableName
	 * @param name
	 * @param other
	 * @return
	 */
	private String buildDicDataCacheName(String tableName, String name, String... other) {
		String cacheName = tableName + ":" + name;
		for (String o : other) {
			if (!VTools.StringIsEmpty(o)) {
				cacheName += "@" + o;
			}
		}
		return cacheName;
	}


	/**
	 * 组装where sql
	 *
	 * @param where
	 * @param newCondition
	 * @return
	 */
	private String buildWhereSql(StringBuilder where, String newCondition) {
		if (where == null) {
			where = new StringBuilder("");
		}
		if (VTools.StringIsEmpty(where.toString())) {
			where.append(" where ");
		} else {
			where.append(" and ");
		}
		where.append(newCondition);
		return where.toString();
	}

	/**
	 * 处理字典属性
	 * @param properties
	 */
	private List<Map<String,String>> buildDicProperties(List<Map<String,String>> properties){
		if(VTools.ListIsEmpty(properties)){
			return new ArrayList<>();
		}
		List<Map<String,String>> ls = new ArrayList<>();
		A : for(Map<String,String> p1 : properties){
			boolean isSame = false;
			B : for(Map<String,String> p2 : ls){
				String p1TableAlias = VTools.StringIsEmpty(p1.get(DicConfigConstants.key_tableAlias))
					?
					"a"
					: p1.get(DicConfigConstants.key_tableAlias);
				String p2TableAlias = VTools.StringIsEmpty(p2.get(DicConfigConstants.key_tableAlias))
					?
					"a"
					: p2.get(DicConfigConstants.key_tableAlias);
				if(p1TableAlias.equals(p2TableAlias)
					&& (p1.get(DicConfigConstants.key_id)).equals(p2.get(DicConfigConstants.key_id))
					&& (p1.get(DicConfigConstants.key_alias)).equals(p2.get(DicConfigConstants.key_alias))){
					isSame = true;
					continue A;
				}
				if(!(p1TableAlias + "." + p1.get(DicConfigConstants.key_id)).equals(p2TableAlias + "." + p2.get(DicConfigConstants.key_id))
					&& (p1.get(DicConfigConstants.key_alias)).equals(p2.get(DicConfigConstants.key_alias))){
					throw new BusinessException("已有相同别名alias["+p2.get(DicConfigConstants.key_alias)+"]");
				}
			}
			if(!isSame){
				ls.add(p1);
			}
		}
		return ls;
	}
}
