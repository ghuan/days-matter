package com.msf.dao.imp;

import com.msf.common.core.exception.BusinessException;
import com.msf.common.core.util.BeanUtils;
import com.msf.common.core.util.ClassUtils;
import com.msf.common.core.util.VTools;
import com.msf.dao.IBaseDao;
import com.msf.jpa.transform.MyAliasToBeanResultTransformer;
import com.msf.data.vo.PageVO;
import org.hibernate.Session;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
* 通用数据操作dao，由spring管理
 */
@Repository
public class BaseDaoImpl implements IBaseDao {

	public BaseDaoImpl(){}
	public BaseDaoImpl(EntityManager entityManager){
		this.entityManager = entityManager;
	}

	@PersistenceContext()
	private EntityManager entityManager;
	@Override
	public EntityManager getEntityManager(){
		return this.entityManager;
	}
	@Override
	public Session getSession() {
		return entityManager.unwrap(Session.class);
	}

	@Override
	public EntityManager createEntityManager(){
		return entityManager.getEntityManagerFactory().createEntityManager();
	}
	@Override
	public void save(Object bean) {
		if(entityManager.contains(bean)){
			entityManager.detach(bean);
		}
		entityManager.persist(bean);
		entityManager.flush();
	}
	@Override
	public <T> T update(T bean) {
		//校验主键是否为空
		getPkeyValue(bean,true);
		T t =  entityManager.merge(bean);
		entityManager.flush();
		return t;
	}

	@Override
	public <T> T update(Class<T> beanClass, Map<String, Object> beanMap) {
		//获取实体类主键
		Object pkeyValue = getPkeyValue(beanMap,beanClass,true);
		//查找
		T queryBean = find(beanClass,pkeyValue);
		if(queryBean == null){
			throw new IdentifierGenerationException( "ids for this class find no data:"+beanClass.getName());
		}
//		evict(queryBean);//切换游离状态
		//beanMap'properties apply to queryBean
		//queryBean 有改动，hibernate会自动提交
		BeanUtils.mapApplyBean(queryBean,beanMap);
		entityManager.flush();
		return queryBean;
	}

	@Override
	public void remove(Class beanClass,Object primaryKey) {
		Object t = entityManager.getReference(beanClass,primaryKey);
		entityManager.remove(t);
		entityManager.flush();
	}

	@Override
	public void remove(Object bean) {
		if(entityManager.contains(bean)){
			entityManager.remove(bean);
			entityManager.flush();
		}else {
			Object pkeyValue = getPkeyValue(bean,true);
			remove(bean.getClass(),pkeyValue);
		}
	}

	@Override
	public <T> T find(Class<T> beanClass,Object primaryKey) {
		return entityManager.find(beanClass,primaryKey);
	}

	@Override
	public Object getPkeyValue(Map<String, Object> beanMap,Class beanClass) {
		return getPkeyValue(beanMap,beanClass,false);
	}

	@Override
	public Object getPkeyValue(Map<String, Object> beanMap, Class beanClass, boolean validateEmpty) {
		return getPkeyValue(BeanUtils.mapToBean(beanMap,beanClass),validateEmpty);
	}

	@Override
	public Object getPkeyValue(Object bean) {
		return getPkeyValue(bean,false);
	}

	@Override
	public Object getPkeyValue(Object bean, boolean validateEmpty) {
		//获取实体类主键
		Object pkey = null;
		Class beanClass = bean.getClass();
		try{
			//获取联合主键
			Map<String,Object> pkeysMap = (Map<String, Object>) ClassUtils.getAnnotationAttributes(beanClass, IdClass.class);
			if(pkeysMap != null && pkeysMap.containsKey("value")){//有联合主键
				Class idClass = (Class) pkeysMap.get("value");
				pkey = idClass.newInstance();
				List<String> emptyIdMsg = new ArrayList<>();
				for (Field idField : idClass.getDeclaredFields()){
					for (Field beanField : beanClass.getDeclaredFields()){
						if(idField.getName().equals(beanField.getName())){
							boolean idFieldAccessible = idField.isAccessible();
							boolean beanFieldAccessible = beanField.isAccessible();
							idField.setAccessible(true);
							beanField.setAccessible(true);
							if(validateEmpty && VTools.StringIsEmpty(beanField.get(bean) + "")){
								emptyIdMsg.add(beanField.getName()+" is empty");
							}else {
								idField.set(pkey,beanField.get(bean));
							}
							idField.setAccessible(idFieldAccessible);
							beanField.setAccessible(beanFieldAccessible);
							break;
						}
					}
				}
				if(!VTools.ListIsEmpty(emptyIdMsg)){
					throw new BusinessException(beanClass.getName()+": identifier ["+String.join(",", emptyIdMsg)+"]");
				}
			}else {//获取单个主键
				List<Field> PKeysSet = ClassUtils.getAnnotationFields(beanClass, Id.class);
				if(PKeysSet.isEmpty()){
					throw new IdentifierGenerationException("identifier not found");
				}
				if(PKeysSet.size() > 1){
					throw new IdentifierGenerationException(" has more than one identifier,but not set idClass");
				}
				for (Field beanField : beanClass.getDeclaredFields()){
					if(PKeysSet.get(0).getName().equals(beanField.getName())){
						boolean beanFieldAccessible = beanField.isAccessible();
						beanField.setAccessible(true);
						pkey = beanField.get(bean);
						if(validateEmpty && VTools.StringIsEmpty(pkey + "")){
							throw new BusinessException(beanClass.getName()+": identifier ["+beanField.getName()+" is empty]");
						}
						beanField.setAccessible(beanFieldAccessible);
						break;
					}
				}
			}
			return pkey;
		}catch (Exception e){
			throw new BusinessException(e.getMessage(),e);
		}
	}

	@Override
	public int executeSql(String sql, Map<String,Object> parameters) {
		Query query = entityManager.createNativeQuery(sql);
		setQueryParameters(query, parameters);
		return query.executeUpdate();
	}

	@Override
	public int executeHql(String hql, Map<String,Object> parameters) {
		Query query = entityManager.createQuery(hql);
		setQueryParameters(query, parameters);
		return query.executeUpdate();
	}

	@Override
	public List<Map<String, Object>> doSqlQuery(String sql, Map<String,Object> parameters) {
		Query query = entityManager.createNativeQuery(sql);
		setQueryParameters(query, parameters);
		return query.unwrap(NativeQueryImpl.class)
			.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@Override
	public <T> List<T> doSqlQuery(String sql, Class<T> clazz, Map<String,Object> parameters) {
		Query query = entityManager.createNativeQuery(sql);
		setQueryParameters(query, parameters);
//		query.unwrap(clazz);
		return query.unwrap(NativeQueryImpl.class).setResultTransformer(new MyAliasToBeanResultTransformer(clazz)).getResultList();
	}

	@Override
	public PageVO<Map<String, Object>> doSqlPage(String sql, Integer pageNo, Integer pageSize, Map<String,Object> parameters) {
		PageVO<Map<String, Object>> pageVO = new PageVO<>();
		pageVO.setPageNo(pageNo);
		pageVO.setPageSize(pageSize);
		Long totalCount = countSql(sql, parameters);
		if (totalCount == null || totalCount == 0) {
			pageVO.setTotalCount(0L);
			return pageVO;
		}
		pageVO.setTotalCount(totalCount);
		Query query = entityManager.createNativeQuery(sql);
		setQueryParameters(query,parameters);
		if (pageNo != null && pageSize != null) {
			if (pageNo == 0)
				pageNo = 1;
			query.setFirstResult((pageNo - 1) * pageSize);
			query.setMaxResults(pageSize);
		}
		List<Map<String, Object>> result = query.unwrap(NativeQueryImpl.class)
			.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		pageVO.setData(result);
		return pageVO;
	}

	@Override
	public <T> PageVO<T> doSqlPage(String sql, Class<T> clazz, Integer pageNo, Integer pageSize, Map<String, Object> parameters) {
		PageVO<T> pageVO = new PageVO<>();
		pageVO.setPageNo(pageNo);
		pageVO.setPageSize(pageSize);
		Long totalCount = countSql(sql, parameters);
		if (totalCount == null || totalCount == 0) {
			pageVO.setTotalCount(0L);
			return pageVO;
		}
		pageVO.setTotalCount(totalCount);
		Query query = entityManager.createNativeQuery(sql);
		setQueryParameters(query,parameters);
		if (pageNo != null && pageSize != null) {
			if (pageNo == 0)
				pageNo = 1;
			query.setFirstResult((pageNo - 1) * pageSize);
			query.setMaxResults(pageSize);
		}
		List<T> result = query.unwrap(NativeQueryImpl.class).setResultTransformer(new MyAliasToBeanResultTransformer(clazz)).getResultList();
		pageVO.setData(result);
		return pageVO;
	}

	@Override
	public Map<String, Object> doSqlLoad(String sql, Map<String,Object> parameters) {
		//获取数据
		List<Map<String, Object>> records = doSqlQuery(sql,parameters);
		if(records.size() == 0){
			return null;
		}
		return records.get(0);
	}

	@Override
	public <T> T doSqlLoad(String sql, Class<T> clazz, Map<String, Object> parameters) {
		//获取数据
		List<T> records = doSqlQuery(sql,clazz,parameters);
		if(records.size() == 0){
			return null;
		}
		return records.get(0);
	}

	@Override
	public <T> List<T> doQuery(String hql, Map<String,Object> parameters) {
		//获取数据
		Query query = entityManager.createQuery(hql);
		setQueryParameters(query, parameters);
		List<T> records = query.getResultList();
		return records;
	}

	@Override
	public <T> PageVO<T> doPage(String hql, Integer pageNo, Integer pageSize, Map<String,Object> parameters) {
		//执行count操作
		PageVO<T> pageVO = new PageVO<>();
		pageVO.setPageNo(pageNo);
		pageVO.setPageSize(pageSize);
		Long totalCount = countHql(hql, parameters);
		if (totalCount == null || totalCount == 0) {
			pageVO.setTotalCount(0L);
			return pageVO;
		}
		pageVO.setTotalCount(totalCount);

		//获取数据
		Query query = entityManager.createQuery(hql);
		setQueryParameters(query,parameters);
		if (pageNo != null && pageSize != null) {
			if (pageNo == 0)
				pageNo = 1;
			query.setFirstResult((pageNo - 1) * pageSize);
			query.setMaxResults(pageSize);
		}
		List<T> records = query.getResultList();
		pageVO.setData(records);
		return pageVO;
	}

	@Override
	public <T> T doLoad(String hql, Map<String,Object> parameters) {
		//获取数据
		List<T> records = doQuery(hql,parameters);
		if(records.size() == 0){
			return null;
		}
		return records.get(0);
	}

	@Override
	public Long countHql(String hql, Map<String,Object> parameters) {
		int fromIndex = hql.toLowerCase().indexOf("from");
		String tempHql = "select count(1) " + hql.substring(fromIndex);
		int orderByIndex = tempHql.toLowerCase().lastIndexOf("order by");
		if(orderByIndex != -1){
			tempHql = tempHql.substring(0,orderByIndex);
		}
		Query query = entityManager.createQuery(tempHql);
		setQueryParameters(query, parameters);
		return ((Long) query.getSingleResult());
	}

	/**
	 * 设置查询参数。
	 *
	 * @param query
	 * @param parameters
	 */
	private void setQueryParameters(Query query, Map<String,Object> parameters) {
		Set<Parameter<?>> pms = query.getParameters();
		if (parameters != null
			&& !parameters.isEmpty()
			&& pms != null
			&& !pms.isEmpty()) {
			for(Parameter pm : pms){
				String key = pm.getName();
				if(parameters.containsKey(key)){
					if (parameters.get(key) instanceof Object[]) {
						List params = new ArrayList();
						for (Object o : (Object[]) parameters.get(key)) {
							params.add(o == null ? "" : o);
						}
						query.setParameter(key, params);
					} else {
						query.setParameter(key, parameters.get(key) == null ? "" : parameters.get(key));
					}
				}
			}
		}
	}

	@Override
	public Long countSql(String sql, Map<String,Object> parameters) {
		String tempSql = "select count(1) from (" + sql + ") tmp";
		Query query = entityManager.createNativeQuery(tempSql);
		setQueryParameters(query, parameters);
		Long num = null;
		Object result = query.getSingleResult();
		try{
			num = ((Long) result).longValue();
		}catch (Exception e){
			try{
				num = ((BigDecimal) result).longValue();
			}catch (Exception e1){
				try{
					num = ((Integer) result).longValue();
				}catch (Exception e2){
					num = ((BigInteger) result).longValue();
				}
			}
		}
		return num;
	}

	@Override
	public Long countSql(String tableName, String where, Map<String,Object> parameters) {
		return countSql("select 1 from "+tableName+" where " + where,parameters);
	}

}
