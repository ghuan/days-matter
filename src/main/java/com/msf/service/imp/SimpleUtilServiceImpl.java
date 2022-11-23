package com.msf.service.imp;

import com.msf.common.core.constant.DicDataConstants;
import com.msf.common.core.constant.SchemaConfigConstants;
import com.msf.common.core.exception.BusinessException;
import com.msf.common.core.util.BeanUtils;
import com.msf.common.core.util.DBFunctionUtils;
import com.msf.common.core.util.TreeConverter;
import com.msf.common.core.util.VTools;
import com.msf.dao.IBaseDao;
import com.msf.service.IDicUtilService;
import com.msf.service.ISchemaUtilService;
import com.msf.service.ISimpleUtilService;
import com.msf.util.EntityUtils;
import com.msf.util.cnd.CndAnalysis;
import com.msf.data.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimpleUtilServiceImpl implements ISimpleUtilService {

	@Autowired
	private IBaseDao dao;
	@Autowired
	private ISchemaUtilService schemaUtilService;
	@Autowired
	private IDicUtilService dicUtilService;

	@Override
	public Map<String, Object> simpleQuery(Map req){
		if(!req.containsKey("schemaCode")){
			throw new BusinessException("schemaCode不能为空!");
		}
		try{
			String schemaCode = req.get("schemaCode")+"";
			//获取schema
			Map<String, Object> schema = schemaUtilService.getSchema(schemaCode);
			List<Map<String,Object>> dataItems = schemaUtilService.getSchemaDataItems(schema);
			//返回值
			Map<String, Object> res = new HashMap<>();
			List<Object> cnd = null;
			String sort = "";
			//请求入参设置的cnd
			if(req.containsKey(SchemaConfigConstants.cnd) && null != req.get(SchemaConfigConstants.cnd)){
				cnd = (List<Object>)req.get("cnd");
			}
			if (req.containsKey(SchemaConfigConstants.sort)){
				sort = req.get(SchemaConfigConstants.sort)+"";
			}
			String queryHQL = getQueryHQL(schema,cnd,sort);
			if(req.containsKey("pageSize") && req.containsKey("pageNo")){
				int pageSize = Integer.parseInt(req.get("pageSize")+"");
				int pageNo = Integer.parseInt(req.get("pageNo")+"");
				PageVO pageVO = dao.doPage(queryHQL,pageNo,pageSize,null);
				List data = pageVO.getData();
				dicUtilService.addDicText(dataItems,data);
				return BeanUtils.beanToMap(pageVO);
			}else{
				List<Map<String, Object>> data= dao.doQuery(queryHQL,null);
				dicUtilService.addDicText(dataItems,data);
				res.put("data",data);
				return res;
			}
		}catch (Exception e){
			e.printStackTrace();
			throw new BusinessException(e.getMessage(),e);
		}
	}

	@Override
	public Map<String, Object> addDicText(Map req){
		return dicUtilService.addDicText(req);
	}


	@Override
	public Map<String, Object> simpleLoad(Map req){
		if(!req.containsKey("schemaCode")){
			throw new BusinessException("schemaCode不能为空!");
		}
		try{
			String schemaCode = req.get("schemaCode")+"";
			//获取schema
			Map<String, Object> schema = schemaUtilService.getSchema(schemaCode);
			List<Map<String,Object>> dataItems = schemaUtilService.getSchemaDataItems(schema);
			//返回值
			Map<String, Object> res = new HashMap<>();
			List<Object> cnd = null;
			String sort = "";
			//请求入参设置的cnd
			if(req.containsKey(SchemaConfigConstants.cnd) && null != req.get(SchemaConfigConstants.cnd)){
				cnd = (List<Object>)req.get("cnd");
			}
			if (req.containsKey(SchemaConfigConstants.sort)){
				sort = req.get(SchemaConfigConstants.sort)+"";
			}
			String queryHQL = getQueryHQL(schema,cnd,sort);
			List<Map<String, Object>> data= dao.doQuery(queryHQL,null);
			if(data.size() == 0){
				throw new BusinessException("数据加载失败!没有符合条件的数据!");
			}
			if(data.size() > 1){
				throw new BusinessException("数据加载失败!有多条符合条件的数据!");
			}
			dicUtilService.addDicText(dataItems,data);
			res.put("data",data.get(0));
			return res;
		}catch (Exception e){
			e.printStackTrace();
			throw new BusinessException(e.getMessage(),e);
		}
	}
	/**
	 * 基本保存
	 * @param req
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = {Exception.class})
	public Map<String, Object> simpleSave(Map req) {
		if(!req.containsKey("schemaCode")){
			throw new BusinessException("schemaCode不能为空!");
		}
		if(!req.containsKey("data")){
			throw new BusinessException("保存数据不能为空!保存失败!");
		}
		if(!req.containsKey("op")){
			throw new BusinessException("op操作类型不能为空!保存失败!");
		}
		String op = req.get("op") +"";
		Map<String,Object> data = (HashMap<String, Object>)req.get("data");
		try{
			String schemaCode = req.get("schemaCode")+"";
//			Map<String, Object> schema = (Map<String, Object>)redisTemplate.opsForValue().get(CacheNameConstants.Schema+schemaCode);
			Map<String, Object> schema = schemaUtilService.getSchema(schemaCode);
			//权限控制
			if(schema.containsKey(SchemaConfigConstants.permissions)){
				Object permissions = schema.get(SchemaConfigConstants.permissions);
				if(!hasPermissions(permissions)){
					throw new BusinessException("不允许访问");
				}
			}
			if (!schema.containsKey(SchemaConfigConstants.entityName)){
				throw new BusinessException("schema["+schemaCode+"]的entityName不能为空!");
			}
			String entityName = schema.get(SchemaConfigConstants.entityName)+"";
			Class c = EntityUtils.getEntityClass(entityName);
			Object entity = null;
			if("create".equals(op)){
//				if(req.containsKey("sameValidation")){
//					List<Map<String,String>> sameValidation = (List<Map<String,String>>)req.get("sameValidation");
//					Map<String,Object> parameters = new HashMap<String,Object>();
//					StringBuilder where = new StringBuilder(" where ");
//					StringBuilder tsxx = new StringBuilder(" ");
//					for(int i = 0 ;i < sameValidation.size();i++){
//						String key = sameValidation.get(i).get("key");
//						String text = sameValidation.get(i).get("text");
//						where.append(key).append("=:").append(key);
//						tsxx.append(" ").append(text).append(" ");
//						if(sameValidation.size()>(i+1)){
//							where.append(" and ");
//						}
//						parameters.put(key,data.get(key));
//					}
//					long cou = dao.countHql("select 1 from "+entityName+where,parameters);
//					if(cou>0){
//						throw new BusinessException(tsxx+"已有相同的数据存在,不能保存");
//					}
//				}
				entity = BeanUtils.mapToBean(data,c);
				dao.save(entity);
			}else if("update".equals(op)){
//				if(req.containsKey("sameValidation")){
//					List<Map<String,String>> sameValidation = (List<Map<String,String>>)req.get("sameValidation");
//					Map<String,Object> parameters = new HashMap<String,Object>();
//					StringBuilder where = new StringBuilder(" where ");
//					StringBuilder tsxx = new StringBuilder(" ");
//					for(int i = 0 ;i < sameValidation.size();i++){
//						String key = sameValidation.get(i).get("key");
//						String text = sameValidation.get(i).get("text");
//						where.append(key).append("=").append(data.get(key));
//						tsxx.append("[").append(text).append("]");
//						if(sameValidation.size()>(i+1)){
//							where.append(" and ");
//						}
//						parameters.put(key,data.get(key));
//					}
//					List<Map<String,String>> items = (List<Map<String,String>>)schema.get(SchemaConfigConstants.items);
//					for(int i = 0 ;i < items.size();i++){
//						Map<String,String> item = items.get(i);
//						if(item.containsKey(SchemaConfigConstants.item_pkey)){
//							String key = item.get(SchemaConfigConstants.item_id);
//							where.append(" and ").append(key).append("<>").append(data.get(key));
//							parameters.put(key,data.get(key));
//						}
//					}
//					long cou = dao.countHql("select 1 from "+entityName+where,null);
//					if(cou>0){
//						throw new BusinessException(tsxx+"已有相同的数据存在,不能保存");
//					}
//				}
				entity = dao.update(c,data);
			}
			String tableName = EntityUtils.getTableName(c);
			dicUtilService.deleteDicByTableName(tableName);
			Map<String, Object> res = new HashMap<String,Object>();
			res.put("body",BeanUtils.beanToMap(entity));
			return res;
		} catch (Exception e){
			e.printStackTrace();
			throw new BusinessException(e.getMessage(),e);
		}
	}
	/**
	 * 基本删除
	 * @param req
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = {Exception.class})
	public void simpleRemove(Map req) {
		if(!req.containsKey("schemaCode")){
			throw new BusinessException("schemaCode不能为空!");
		}
		if(!req.containsKey("data")){
			throw new BusinessException("删除数据不能为空!删除失败!");
		}
		Object data = req.get("data");

		try{
			String schemaCode = req.get("schemaCode")+"";
//			Map<String, Object> schema = (Map<String, Object>)redisTemplate.opsForValue().get(CacheNameConstants.Schema+schemaCode);
			Map<String, Object> schema = schemaUtilService.getSchema(schemaCode);
			//权限控制
			if(schema.containsKey(SchemaConfigConstants.permissions)){
				Object permissions = schema.get(SchemaConfigConstants.permissions);
				if(!hasPermissions(permissions)){
					throw new BusinessException("不允许访问");
				}
			}
			if (!schema.containsKey(SchemaConfigConstants.entityName)){
				throw new BusinessException("schema["+schemaCode+"]的entityName不能为空!");
			}
			String entityName = schema.get(SchemaConfigConstants.entityName)+"";
			Class c = EntityUtils.getEntityClass(entityName);
			if(data instanceof HashMap){
				Object entity = BeanUtils.mapToBean((Map<String, Object>) data,c);
				dao.remove(entity);
			}else if(data instanceof ArrayList){
				for(Map<String,Object> d : (ArrayList<Map<String,Object>>)data){
					Object entity = BeanUtils.mapToBean(d,c);
					dao.remove(entity);
				}
			}

			String tableName = EntityUtils.getTableName(c);
			dicUtilService.deleteDicByTableName(tableName);
		}catch (Exception e){
			e.printStackTrace();
			throw new BusinessException(e.getMessage(),e);
		}
	}

	@Override
	public String getQueryHQL(Map<String, Object> schema,List<Object> cnd,String sort) {
		try{
			if (!schema.containsKey(SchemaConfigConstants.entityName)){
				throw new BusinessException("entityName不能为空!");
			}
			String entityName = schema.get(SchemaConfigConstants.entityName)+"";
			int tableNameAliasAscii = 97;//table别名 ascii码表示 97=a
			List<Map<String,Object>> crossJoinEntities = new ArrayList<>();//交叉连接实体类列表
			List<Map<String,Object>> otherJoinEntities = new ArrayList<>();//其他连接实体类列表
			Map<String,Object> mainEntity = new HashMap<>();
			mainEntity.put(SchemaConfigConstants.relations_entityName,entityName);
			mainEntity.put(SchemaConfigConstants.relations_parent,null);//关联的父表 null为主表
			mainEntity.put(SchemaConfigConstants.item_entityAlias,((char)tableNameAliasAscii+""));//表别名
			tableNameAliasAscii++;
//			mainEntity.put(SchemaJsonProConstants.relations_cnd,mainCnd);//主表的cnd条件
			crossJoinEntities.add(mainEntity);

			/**
			 * 多表关联
			 * relations 配置说明 entityName joinMethod parent cnd
			 * entityName不能为空
			 * 默认cross交叉连接 joinMethod可配置 left right cross
			 * 交叉连接关系，hql拼接用逗号关联，hibernate执行hql时会自动转成cross join
			 */
			List<Map<String,Object>> relations = (List<Map<String,Object>> )schema.get(SchemaConfigConstants.relations);
			if(!VTools.ListIsEmpty(relations)){
				for(int i=0;i<relations.size();i++){
					Map<String,Object> relation = relations.get(i);
					if (!relation.containsKey(SchemaConfigConstants.relations_entityName)){
						throw new BusinessException("关联表"+(char) tableNameAliasAscii+"entityName不能为空!");
					}
					Map<String,Object> relationEntity = new HashMap<>();
					String relationEntityName = relation.get(SchemaConfigConstants.relations_entityName)+"";
					String joinMethod = relation.containsKey(SchemaConfigConstants.relations_joinMethod)?
							relation.get(SchemaConfigConstants.relations_joinMethod)+""
							: "cross";//默认交叉连接
					String entityAlias = (char)tableNameAliasAscii+"";
					tableNameAliasAscii++;
					relation.put(SchemaConfigConstants.item_entityAlias,entityAlias);

					//关联条件
					if(relation.containsKey(SchemaConfigConstants.relations_cnd)){
						List<Object> joinCnd = (List<Object>)relation.get("cnd");
						relationEntity.put(SchemaConfigConstants.relations_cnd,joinCnd);
					}
					//放入实体类名
					relationEntity.put(SchemaConfigConstants.relations_entityName,relationEntityName);
					//放入关联类型
					relationEntity.put(SchemaConfigConstants.relations_joinMethod,joinMethod);
					//放入表别名
					relationEntity.put(SchemaConfigConstants.item_entityAlias,entityAlias);

					String parent = "";
					if("cross".equals(joinMethod)){
						//放入关联的父表
						relationEntity.put(SchemaConfigConstants.relations_parent,null);
						crossJoinEntities.add(relationEntity);
					}else {
						if(relation.containsKey(SchemaConfigConstants.relations_parent)){
							parent = relation.get(SchemaConfigConstants.relations_parent)+"";
						}else {
							if(i == 0){
								parent = mainEntity.get(SchemaConfigConstants.item_entityAlias)+"";
							}else {
								parent = relations.get(i-1).get(SchemaConfigConstants.item_entityAlias)+"";//默认关联上一张表
							}
						}
						//放入关联的父表
						relationEntity.put(SchemaConfigConstants.relations_parent,parent);
						otherJoinEntities.add(relationEntity);
					}
				}
			}
			if(!VTools.ListIsEmpty(otherJoinEntities)){
				otherJoinEntities = TreeConverter.instance()
						.setConverterType(TreeConverter.TYPE_DEFAULT)
						.setKeyName(SchemaConfigConstants.item_entityAlias)
						.setParentKeyName(SchemaConfigConstants.relations_parent)
						.getTree(otherJoinEntities);
			}

			List<Map<String,Object>> dataItems = schemaUtilService.getSchemaDataItems(schema);
			//组装hql
			StringBuilder queryHQL = new StringBuilder("select ");
			for(int i = 0 ; i < dataItems.size(); i++){
				Map<String,Object> item = dataItems.get(i);
				if(!item.containsKey(SchemaConfigConstants.item_id)){
					throw new BusinessException("item:"+item.get(SchemaConfigConstants.item_text)+" [配置没有id项]!");
				}
				if(i==0){//设置 hql 返回 List<Map>
					queryHQL.append("new map(");
				}
				if(i!=0){
					queryHQL.append(",");
				}
				//查询字段
				String itemId = item.get(SchemaConfigConstants.item_id)+"";
				String item_entityAlias = item.get(SchemaConfigConstants.item_entityAlias)+"";
				if(!VTools.StringIsEmpty(item_entityAlias)){
					itemId = item_entityAlias+"."+itemId;
				}else {//默认查询主表的字段
					itemId = mainEntity.get(SchemaConfigConstants.item_entityAlias)+"."+itemId;
				}
				//查询字段特殊处理
				if(item.containsKey(SchemaConfigConstants.item_virtual) && (boolean)item.get(SchemaConfigConstants.item_virtual)){//虚拟字段
					queryHQL.append("cast(null as string)");
				}else if(item.containsKey(SchemaConfigConstants.item_type)){
					String itemType = item.get(SchemaConfigConstants.item_type)+"";
					switch (itemType){
						case "date":
							queryHQL.append(DBFunctionUtils.DATE_FORMAT(itemId,"yyyy-MM-dd"));
							break;
						case "datetime":
							queryHQL.append(DBFunctionUtils.DATE_FORMAT(itemId,"yyyy-MM-dd HH:mm:ss"));
							break;
						case "boolean":
							queryHQL.append("case ").append(itemId).append(" when 0 then false when null then false when 1 then true else ").append(itemId).append(" end");
							break;
						default:
							queryHQL.append(itemId);
					}
				}else {
					queryHQL.append(itemId);
				}
				queryHQL.append(" as ");
				String item_alias = item.get(SchemaConfigConstants.item_alias)+"";
				if(!VTools.StringIsEmpty(item_alias)){//查询字段的别名
					queryHQL.append(item_alias);
				}else {
					queryHQL.append(item.get(SchemaConfigConstants.item_id));
				}
				if (i == dataItems.size() -1){
					queryHQL.append(")");
				}
			}

			// 组装where条件
			StringBuilder where = new StringBuilder("");
			//请求入参设置的cnd
			if(cnd != null && cnd.size() > 0){
				buildWhereHql(where, CndAnalysis.instance().toString(cnd));
			}
			//schema 自带的cnd 主表默认的cnd
			if(schema.containsKey(SchemaConfigConstants.cnd)){
				List<Map<String,Object>> schemaCnd = (List<Map<String,Object>>)schema.get(SchemaConfigConstants.cnd);
				if(schemaCnd != null && schemaCnd.size() > 0){
					buildWhereHql(where,CndAnalysis.instance().toString(schemaCnd));
				}
			}


			//组装from 表关联
			StringBuilder from = new StringBuilder();
			for(int i=0;i<crossJoinEntities.size();i++){
				Map<String,Object> crossJoinEntity = crossJoinEntities.get(i);
				if(i == 0){//主表
					from.append(" from ");
				}
				from.append(crossJoinEntity.get(SchemaConfigConstants.relations_entityName))
						.append(" ")
						.append(crossJoinEntity.get(SchemaConfigConstants.item_entityAlias));
				//组装当前表的其他关联表
				buildOtherJoinFromHQL(crossJoinEntity,from,otherJoinEntities);
				if(i < crossJoinEntities.size() - 1){
					from.append(",");
				}
				//交叉连接的cnd 条件放在where 里
				if(crossJoinEntity.containsKey(SchemaConfigConstants.relations_cnd)){
					List<Object> relation_cnd = (List<Object>) crossJoinEntity.get(SchemaConfigConstants.relations_cnd);
					if(relation_cnd!= null && relation_cnd.size() > 0){//组装where条件
						buildWhereHql(where,CndAnalysis.instance().toString(relation_cnd));
					}
				}

			}
			queryHQL.append(from);
			queryHQL.append(where);
			if (VTools.StringIsEmpty(sort) && schema.containsKey(SchemaConfigConstants.sort)){
				sort = schema.get(SchemaConfigConstants.sort)+"";
			}
			if(!VTools.StringIsEmpty(sort)){
				queryHQL.append(" order by ").append(sort);
			}
			return queryHQL.toString();
		}catch (Exception e){
			e.printStackTrace();
			throw new BusinessException(e.getMessage(),e);
		}
	}

	private void buildOtherJoinFromHQL(Map<String,Object> parentEntity,StringBuilder from,List<Map<String,Object>> otherJoinEntities){
		for(Map<String,Object> otherEntity:otherJoinEntities) {
			if ((otherEntity.get(SchemaConfigConstants.relations_parent)).equals(parentEntity.get(SchemaConfigConstants.item_entityAlias))) {
				String joinMethod = otherEntity.get(SchemaConfigConstants.relations_joinMethod)+"";
				//组装join 连接hql
				from.append(" ")
						.append(joinMethod.toLowerCase().trim())
						.append(" join ")
						.append(otherEntity.get(SchemaConfigConstants.relations_entityName))
						.append(" ")
						.append(otherEntity.get(SchemaConfigConstants.item_entityAlias));
				if(otherEntity.containsKey(SchemaConfigConstants.relations_cnd)){
					List<Object> relation_cnd = (List<Object>) otherEntity.get(SchemaConfigConstants.relations_cnd);
					if(relation_cnd!= null && relation_cnd.size() > 0){
						from.append(" on ")
								.append(CndAnalysis.instance().toString(relation_cnd));
					}else {
						throw new BusinessException("relation entity["+otherEntity.get(SchemaConfigConstants.relations_entityName)+"] has empty cnd");
					}
				}else {
					throw new BusinessException("relation entity["+otherEntity.get(SchemaConfigConstants.relations_entityName)+"] has empty cnd");
				}
				if(otherEntity.containsKey(DicDataConstants.children)){
					List<Map<String,Object>> children = (List) otherEntity.get(DicDataConstants.children);
					if(!VTools.ListIsEmpty(children)){
						buildOtherJoinFromHQL(otherEntity,from,children);
					}
				}
			}
		}
	}
	@Override
	public String getQueryHQL(String schemaCode,List<Object> cnd,String sort) {
		if(VTools.StringIsEmpty(schemaCode)){
			throw new BusinessException("schemaCode不能为空!");
		}
		try{
			//获取schema
			Map<String, Object> schema = schemaUtilService.getSchema(schemaCode);
			return getQueryHQL(schema,cnd,sort);
		}catch (Exception e){
			e.printStackTrace();
			throw new BusinessException(e.getMessage(),e);
		}
	}

	/**
	 * 判断当前用户是否具有入参包含的权限
	 * @param permissions
	 * @return
	 */
	private boolean hasPermissions(Object permissions){
		return true;
	}

	/**
	 * 组装where hql
	 * @param where
	 * @param newCondition
	 * @return
	 */
	private String buildWhereHql(StringBuilder where,String newCondition){
		if(where == null){
			where = new StringBuilder("");
		}
		if(VTools.StringIsEmpty(where.toString())){
			where.append(" where ");
		}else{
			where.append(" and ");
		}
		where.append(newCondition);
		return where.toString();
	}
}
