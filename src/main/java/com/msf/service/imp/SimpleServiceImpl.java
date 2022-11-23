package com.msf.service.imp;

import com.msf.common.core.dto.AgeDTO;
import com.msf.common.core.exception.BusinessException;
import com.msf.common.core.jdbc.*;
import com.msf.common.core.util.DBFunctionUtils;
import com.msf.common.core.util.PyConverter;
import com.msf.common.core.util.VTools;
import com.msf.common.core.util.WBConverter;
import com.msf.common.core.util.gson.GsonUtils;
import com.msf.dao.IBaseDao;
import com.msf.dao.imp.BaseDaoImpl;
import com.msf.service.IDicUtilService;
import com.msf.service.ISchemaUtilService;
import com.msf.service.ISimpleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SimpleServiceImpl implements ISimpleService {

	@Autowired
	private IBaseDao dao;
	@Autowired
	private ISchemaUtilService schemaUtilService;
	@Autowired
	private IDicUtilService dicUtilService;

	@Override
	public AgeDTO getAge(String birthday) {
		try {
			Date birth = new SimpleDateFormat("yyyy-MM-dd").parse(birthday);
			return VTools.getAge(birth);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> getCode(Map req){
		if(!req.containsKey("body")){
			throw new BusinessException("body不能为空!");
		}
		Map<String,Object> body = (Map<String,Object>)req.get("body");
		if(!body.containsKey("codeType")){
			throw new BusinessException("codeType不能为空!");
		}
		List<String> codeType = (List<String>)body.get("codeType");
		if(!body.containsKey("value")){
			throw new BusinessException("value不能为空!");
		}
		String value = body.get("value")+"";
		Map<String,Object> rebody = new HashMap<String,Object>();
		for(int i = 0 ; i < codeType.size() ; i ++){
			String code = codeType.get(i);
			if("py".equals(code)){
				rebody.put("py", PyConverter.getFirstLetter(value));
			}
			if("wb".equals(code)){
				rebody.put("wb", WBConverter.getWBCode(value));
			}
		}
		return rebody;
	}

	@Override
	public String getServerDate() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(new Date());
	}

	@Override
	@Transactional(rollbackFor = {Exception.class})
	public Map<String,Object> executeSQL(Map req) {
		Map<String,Object> rs = new HashMap<>();
		String result = "";
		String sqlContent = req.get("sqlContent")+"";
		boolean commit = (boolean) req.get("commit");
		sqlContent = sqlContent.trim();
		if(VTools.StringIsEmpty(sqlContent)){
			throw new BusinessException("空sql语句");
		}
		String[] sqls = sqlContent.split(";");
		int idx = 0;
		EntityManager em = dao.createEntityManager();
		IBaseDao baseDao = new BaseDaoImpl(em);
        EntityTransaction trs = em.getTransaction();
		trs.begin();
		List<String> commitSQLS = new ArrayList<>();
        try{
			for(String sql : sqls){
				sql = sql.trim();
				String lowerCaseSQL = sql.toLowerCase();
				if(lowerCaseSQL.indexOf("select") == 0){
					List<Map<String,Object>> list = baseDao.doSqlQuery(sql,null);
					result += ++idx + ". 查询结果：\n"+ GsonUtils.toJson(list,false,true);
					result += "\n";
				}else if(lowerCaseSQL.indexOf("insert") == 0
						|| lowerCaseSQL.indexOf("update") == 0
						|| lowerCaseSQL.indexOf("delete") == 0){
					long num = baseDao.executeSql(sql,null);
					result += ++idx + ". 执行影响记录数:" + num + "条";
					result += "\n";
					if(!commit){
						commitSQLS.add(sql);
					}
				}else {
					baseDao.executeSql(sql,null);
					result += ++idx + ". 执行成功.";
					result += "\n";
				}
			}
			if(commit){
				trs.commit();
			}else if(!VTools.ListIsEmpty(commitSQLS)){
				trs.rollback();
			}
		}catch (Exception e){
			trs.rollback();
			throw new BusinessException(e.getMessage(),e);
        }finally {
            em.close();//关闭EntityManager
        }
		rs.put("result",result);
        if(!VTools.ListIsEmpty(commitSQLS)){
			rs.put("shouldCommit",true);
			rs.put("commitSQLContent",String.join(";",commitSQLS));
		}
		return rs;
	}

	@Override
	public Map<String,Object> executeSQLRemote(Map req) {
		Map<String,Object> rs = new HashMap<>();
		String result = "";
		String url = req.get("url")+"";
		String username = req.get("username")+"";
		String password = req.get("password")+"";
		if(VTools.StringIsEmpty(url)){
			throw new BusinessException("jdbc url不能为空");
		}
		if(VTools.StringIsEmpty(username)){
			throw new BusinessException("username不能为空");
		}
		String dbType = DBFunctionUtils.getDataBaseType(url);
		JDBC jdbc;
		if(DBFunctionUtils.MYSQL.equals(dbType)){
			jdbc = new JDBCForMySql(false);
		}else if(DBFunctionUtils.ORACLE.equals(dbType)){
			jdbc = new JDBCForOracle(false);
		}else if(DBFunctionUtils.SQLSERVER.equals(dbType)){
			jdbc = new JDBCForSqlServer(false);
		}else if(DBFunctionUtils.POLARDB.equals(dbType)){
			jdbc = new JDBCForPolardb(false);
		}else {
			throw new BusinessException("未匹配到相应的数据库链接，当前系统仅支持mysql、oracle、sqlserver、polardb数据库连接");
		}
		jdbc.setUrl(url);
		jdbc.setUsername(username);
		jdbc.setPassword(password);
		String sqlContent = req.get("sqlContent")+"";
		boolean commit = (boolean) req.get("commit");
		sqlContent = sqlContent.trim();
		if(VTools.StringIsEmpty(sqlContent)){
			throw new BusinessException("空sql语句");
		}
		String[] sqls = sqlContent.split(";");
		int idx = 0;
		List<String> commitSQLS = new ArrayList<>();
		try{
			for(String sql : sqls){
				sql = sql.trim();
				String lowerCaseSQL = sql.toLowerCase();
				if(lowerCaseSQL.indexOf("select") == 0){
					List<Map<String,Object>> list = jdbc.doSqlQuery(sql,null);
					result += ++idx + ". 查询结果：\n"+ GsonUtils.toJson(list,false,true);
					result += "\n";
				}else if(lowerCaseSQL.indexOf("insert") == 0
					|| lowerCaseSQL.indexOf("update") == 0
					|| lowerCaseSQL.indexOf("delete") == 0){
					int num = jdbc.executeSql(sql,null);
					result += ++idx + ". 执行影响记录数:" + num + "条";
					result += "\n";
					if(!commit){
						commitSQLS.add(sql);
					}
				}else {
					jdbc.executeSql(sql,null);
					result += ++idx + ". 执行成功.";
					result += "\n";
				}
			}
			if(commit){
				jdbc.commit();
			}else if(!VTools.ListIsEmpty(commitSQLS)){
				jdbc.rollback();
			}
		}catch (Exception e){
			jdbc.rollback();
			throw new BusinessException(e.getMessage(),e);
		}finally {
			jdbc.closeConnect();//关闭连接
		}
		rs.put("result",result);
		if(!VTools.ListIsEmpty(commitSQLS)){
			rs.put("shouldCommit",true);
			rs.put("url",url);
			rs.put("username",username);
			rs.put("password",password);
			rs.put("commitSQLContent",String.join(";",commitSQLS));
		}
		return rs;
	}
}
