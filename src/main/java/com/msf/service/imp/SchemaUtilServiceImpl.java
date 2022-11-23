package com.msf.service.imp;

import com.msf.cache.MemoryCache;
import com.msf.common.core.constant.SchemaConfigConstants;
import com.msf.common.core.dto.ResourceDTO;
import com.msf.common.core.exception.BusinessException;
import com.msf.common.core.util.VTools;
import com.msf.common.core.util.gson.GsonUtils;
import com.msf.service.IDicUtilService;
import com.msf.service.ISchemaUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SchemaUtilServiceImpl implements ISchemaUtilService {

	private final String resourceRoot = "schema";//schema文件存放在resources下的根目录名
	private final String resourceSuffix = "sc";//schema文件后缀

	@Autowired
	private IDicUtilService dicUtilService;

	@Override
	public Map<String, Object> getSchema(Map req) {
		if(!req.containsKey("schemaCode")){
			throw new BusinessException("schemaCode为空，获取schema失败!");
		}
		try{
			String schemaCode = req.get("schemaCode")+"";
			Map<String, Object> res = getSchema(schemaCode);
			return res;
		}catch (Exception e){
			e.printStackTrace();
			throw new BusinessException("获取schema信息失败!"+e.getMessage(),e);
		}
	}
	@Override
	public Map<String, Object> getSchema(String schemaCode) {
		if(VTools.StringIsEmpty(schemaCode)){
			throw new BusinessException("schemaCode不能为空");
		}
		String schemaCacheName = buildSchemaCacheName(schemaCode);
		Map<String, Object> res = (Map<String, Object>) MemoryCache.getSchemaCache(schemaCacheName);
		if(res == null){
			res = getLocalSchema(schemaCode);
			MemoryCache.putSchemaCache(schemaCacheName,res);
		}
		List<Map<String, Object>> schemaDataItems = getSchemaDataItems(res);
		for(Map<String,Object> item : schemaDataItems){
			if(item.containsKey(SchemaConfigConstants.item_dic)){
				Map<String, Object> dic = (Map<String, Object>) item.get(SchemaConfigConstants.item_dic);
				if(!dic.containsKey(SchemaConfigConstants.item_dic_data)){
					List<Map<String, Object>> dicData = dicUtilService.getDicData(dic);
					dic.put(SchemaConfigConstants.item_dic_data,dicData);
				}
			}
		}
		return res;
	}

	@Override
	public List<Map<String, Object>> getSchemaDataItems(String schemaCode) {
		Map<String, Object> schema = getSchema(schemaCode);
		return getSchemaDataItems(schema);
	}

	@Override
	public List<Map<String, Object>> getSchemaDataItems(Map<String,Object> schema) {
		if(!schema.containsKey(SchemaConfigConstants.items)){
			throw new BusinessException(SchemaConfigConstants.items + "不能为空!");
		}
		List<Map<String,Object>> schemaItems = (List<Map<String, Object>>) schema.get(SchemaConfigConstants.items);
		List<Map<String, Object>> result = new ArrayList<>();
		getSchemaDataItemsRecursive(schemaItems,result);
		return result;
	}

	@Override
	public Map<String,Object> getLocalSchema(String schemaCode){
		if(VTools.StringIsEmpty(schemaCode)){
			throw new BusinessException("schemaCode不能为空");
		}
		try {
			ResourceDTO resourceDTO = VTools.getResource(resourceRoot+"/"+schemaCode+"."+resourceSuffix);
			if(resourceDTO == null){
				throw new BusinessException("未找到"+schemaCode+",对应的schema!");
			}
			StringBuilder sb = new StringBuilder();
			String line;

			BufferedReader br = new BufferedReader(new InputStreamReader(resourceDTO.getInputStream(),"UTF-8"));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String jsonStr = sb.toString();
			if(!VTools.StringIsEmpty(jsonStr)){
				return GsonUtils.fromJson(jsonStr, Map.class);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String buildSchemaCacheName(String name){
		return name;
	}

	private void getSchemaDataItemsRecursive(List<Map<String,Object>> schemaItems,List<Map<String,Object>> result){
		if(!VTools.ListIsEmpty(schemaItems)){
			for(Map item : schemaItems){
				if(item.containsKey(SchemaConfigConstants.item_items)){
					List<Map<String,Object>> items;
					try{
						items = (List<Map<String, Object>>) item.get(SchemaConfigConstants.item_items);
					}catch (Exception e){
						items = null;
					}
					getSchemaDataItemsRecursive(items,result);
				}else {
					result.add(item);
				}
			}
		}
	}
}
