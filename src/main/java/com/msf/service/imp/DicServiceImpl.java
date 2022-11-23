package com.msf.service.imp;

import com.msf.cache.MemoryCache;
import com.msf.common.core.exception.BusinessException;
import com.msf.common.core.util.BeanUtils;
import com.msf.common.core.util.PyConverter;
import com.msf.common.core.util.WBConverter;
import com.msf.dao.IBaseDao;
import com.msf.data.entity.Dic;
import com.msf.service.IDicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * description：用户service
 */
@Service
public class DicServiceImpl implements IDicService {

	@Autowired
	private IBaseDao dao;

	@Override
	@Transactional(rollbackFor = {Exception.class})
	public void saveDic(Map req) {
		try{
			String op = req.get("op") +"";
			Map<String,Object> data = (HashMap<String, Object>)req.get("data");
			String DIC_CODE = data.get("DIC_CODE")+"";
			Dic dic =  BeanUtils.mapToBean(data, Dic.class);
			dic.setPY(PyConverter.getFirstLetter(dic.getNAME()));
			dic.setWB(WBConverter.getWBCode(dic.getNAME()));
			if("create".equals(op)){
				Map<String, Object> parameters = new HashMap<String,Object>();
				parameters.put("DIC_CODE",dic.getDIC_CODE());
				parameters.put("CODE",dic.getCODE());
				long cun = dao.countSql("SELECT 1 FROM SYS_DIC WHERE DIC_CODE=:DIC_CODE and CODE=:CODE",parameters);
				if(cun>0){
					throw new BusinessException("CODE:"+dic.getCODE()+",已存在!");
				}
				dao.save(dic);
			}else if("update".equals(op)){
				dao.update(dic);
			}
			MemoryCache.removeDicDataCache("sys_dic:" + DIC_CODE);
			MemoryCache.removeDicDataCache("sys_dic:sys_dic@DIC_CODE = '" + DIC_CODE+"'");
		} catch (BusinessException b){
			throw new BusinessException(b.getMessage());
		}
		catch (Exception e){
			e.printStackTrace();
			throw new BusinessException("保存字典明细失败！请联系管理员！",e);
		}
	}

	@Override
	@Transactional(rollbackFor = {Exception.class})
	public void removeDic(Map req) {
		try{
			Map<String,Object> data = (HashMap<String, Object>)req.get("data");
			Map<String, Object> paramsData = new HashMap<String,Object>();
			String DIC_CODE = data.get("DIC_CODE")+"";
			String arr[] = DIC_CODE.split("\\.");
			paramsData.put("DIC_CODE",DIC_CODE);
			paramsData.put("CODE",data.get("CODE"));
			dao.executeSql("DELETE FROM SYS_DIC WHERE DIC_CODE=:DIC_CODE AND CODE=:CODE",paramsData);
			MemoryCache.removeDicDataCache("sys_dic:" + DIC_CODE);
			MemoryCache.removeDicDataCache("sys_dic:sys_dic@DIC_CODE = '" + DIC_CODE+"'");
		} catch (BusinessException b){
			throw new BusinessException(b.getMessage());
		}
		catch (Exception e){
			e.printStackTrace();
			throw new BusinessException("删除字典明细失败！请联系管理员！",e);
		}
	}
}
