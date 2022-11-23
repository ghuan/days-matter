package com.msf.util;

import com.msf.common.core.exception.BusinessException;
import com.msf.common.core.util.ClassUtils;
import com.msf.common.core.util.SpringContextHolder;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by huan.gao on 2019/6/20.
 */
public class EntityUtils {

	/**
	 * 获取当前应用下所有的实体类
	 * @return
	 */
	public static List<Class> getEntityClasses(){
		EntityManagerFactory factory = SpringContextHolder.getBean(EntityManagerFactory.class);
		//取EntityManagerFactory元数据
		Metamodel metamodel = factory.getMetamodel();
		//获取实体类列表
		Set<EntityType<?>> entities = metamodel.getEntities();
		List<Class> entityClasses = entities.stream()
			.map(EntityType::getJavaType)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		return entityClasses;
	}

	/**
	 * 获取当前应用下指定的实体类
	 * @return
	 */
	public static Class getEntityClass(String entityName){
		EntityManagerFactory factory = SpringContextHolder.getBean(EntityManagerFactory.class);
		//取EntityManagerFactory元数据
		Metamodel metamodel = factory.getMetamodel();
		//获取实体类列表
		Set<EntityType<?>> entities = metamodel.getEntities();
		List<Class> entityClasses = entities.stream()
			.map(EntityType::getJavaType)
			.filter(Objects::nonNull)
			.filter(o -> (o.getSimpleName().equals(entityName) || o.getName().equals(entityName)))
			.collect(Collectors.toList());
		if(entityClasses.size() == 0){
			throw new BusinessException("no found entityClass["+entityName+"]");
		}
		if(entityClasses.size() > 1){
			throw new BusinessException("more than one entityClass["+entityName+"]");
		}
		return entityClasses.get(0);
	}

	public static String getTableName(Class entityClass) throws NoSuchFieldException, IllegalAccessException {
		//是否存在Entity注解
		Object entityAnnotate =  ClassUtils.getAnnotationAttributes(entityClass, Entity.class);
		if(entityAnnotate == null){
			throw new BusinessException("this class["+entityClass+"] is not a entity class");
		}
		//获取实体类Table 注解属性
		Map<String,Object> tableAnnotationAttributes = (Map<String, Object>) ClassUtils.getAnnotationAttributes(entityClass, Table.class);
		String tableName = null;
		if(tableAnnotationAttributes.containsKey("name")){
			tableName = tableAnnotationAttributes.get("name").toString();
		}else{//不存在name属性，默认类名与表名一致
			tableName = entityClass.getSimpleName();
		}
		return tableName;
	}
	public static void main(String args[]){
		System.out.println((char)(97));
	}
}
