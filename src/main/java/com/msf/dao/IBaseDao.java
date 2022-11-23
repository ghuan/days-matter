package com.msf.dao;

import com.msf.data.vo.PageVO;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

public interface IBaseDao {
	EntityManager createEntityManager();
	EntityManager getEntityManager();
	/**
	 * 获取hibernate session
	 */
	Session getSession();
	void save(Object bean);
	<T> T update(T bean);
	<T> T update(Class<T> beanClass, Map<String, Object> beanMap);
	void remove(Class beanClass, Object primaryKey);
	void remove(Object bean);
	<T> T find(Class<T> beanClass, Object primaryKey);

	/**
	 * 获取主键值
	 * @param beanClass
	 * @param beanMap
	 * @return
	 */
	Object getPkeyValue(Map<String, Object> beanMap, Class beanClass);

	/**
	 * 获取主键值
	 * @param beanClass
	 * @param beanMap
	 * @param validateEmpty 是否校验主键空值
	 * @return
	 */
	Object getPkeyValue(Map<String, Object> beanMap, Class beanClass, boolean validateEmpty);

	/**
	 * 获取主键值
	 * @param bean
	 * @return
	 */
	Object getPkeyValue(Object bean);

	/**
	 * 获取主键值
	 * @param bean
	 * @param validateEmpty 是否校验主键空值
	 * @return
	 */
	Object getPkeyValue(Object bean, boolean validateEmpty);


	/**
	 * 这个用于执行删除和更新的sql语句
	 *
	 * @param sql
	 * @param parameters
	 */
	int executeSql(String sql, Map<String, Object> parameters);

	/**
	 * 这个用于执行删除和更新的hql语句
	 *
	 * @param hql
	 * @param parameters
	 */
	int executeHql(String hql, Map<String, Object> parameters);

	/**
	 * 根据原始sql语句执行sql
	 *
	 * @param sql    原始sql语句
	 * @param parameters 要传递的参数
	 * @return map对象
	 */
	List<Map<String, Object>> doSqlQuery(String sql, Map<String, Object> parameters);

	/**
	 * @param sql    原始sql语句
	 * @param clazz  要反射的Class对象
	 * @param parameters 要传递的参数
	 * @param <T>
	 * @return
	 */
	<T> List<T> doSqlQuery(String sql, Class<T> clazz, Map<String, Object> parameters);

	/**
	 * 分页显示数据
	 *
	 * @param sql
	 * @param pageNo  第几页
	 * @param pageSize 每页显示多少个
	 * @param parameters   需要传递的参数
	 * @return
	 */
	PageVO<Map<String,Object>> doSqlPage(String sql, Integer pageNo, Integer pageSize, Map<String, Object> parameters);

	/**
	 * 分页显示数据
	 *
	 * @param sql
	 * @param pageNo  第几页
	 * @param pageSize 每页显示多少个
	 * @param parameters   需要传递的参数
	 * @return
	 */
	<T> PageVO<T> doSqlPage(String sql, Class<T> clazz, Integer pageNo, Integer pageSize, Map<String, Object> parameters);

	/**
	 * 执行sql查询语句
	 *
	 * @param sql
	 * @param parameters
	 * @return
	 */
	Map<String, Object> doSqlLoad(String sql, Map<String, Object> parameters);
	/**
	 * 执行sql查询语句
	 *
	 * @param sql
	 * @param parameters
	 * @return
	 */
	<T> T doSqlLoad(String sql, Class<T> clazz, Map<String, Object> parameters);

	/**
	 * 执行hql查询语句 获取一个对象
	 *
	 * @param hql
	 * @param parameters
	 * @return
	 */
	<T> List<T> doQuery(String hql, Map<String, Object> parameters);

	/**
	 * hql的分页操作
	 *
	 * @param hql
	 * @param pageNo
	 * @param pageSize
	 * @param parameters
	 * @return
	 */
	<T> PageVO<T> doPage(String hql, Integer pageNo, Integer pageSize, Map<String, Object> parameters);

	/**
	 * 执行hql查询语句
	 *
	 * @param hql
	 * @param parameters
	 * @return
	 */
	<T> T doLoad(String hql, Map<String, Object> parameters);

	/**
	 * hql执行统计总数
	 *
	 * @param hql
	 * @param parameters
	 * @return
	 */
	Long countHql(String hql, Map<String, Object> parameters);

	/**
	 * 执行统计总数
	 *
	 * @param sql
	 * @param parameters
	 * @return
	 */
	Long countSql(String sql, Map<String, Object> parameters);

	/**
	 * 执行统计总数
	 *
	 * @param tableName
	 * @param where
	 * @param parameters
	 * @return
	 */
	Long countSql(String tableName, String where, Map<String, Object> parameters);

}
