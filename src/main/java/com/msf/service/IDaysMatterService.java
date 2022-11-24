package com.msf.service;

import com.msf.data.po.DaysMatterConfigPO;
import com.msf.data.vo.DaysMatterConfigVO;
import com.msf.data.vo.PageVO;

import java.util.Map;

/**
 *
* description：纪念日接口
 */
public interface IDaysMatterService {
	void call(Boolean openClient);
	PageVO<Map<String,Object>> doQuery(Map<String,Object> req);
	Map<String, Object> doSave(Map req);
	DaysMatterConfigVO getConfig();
	void saveConfig(DaysMatterConfigPO daysMatterConfigPO);
	String getLunarDate(Map<String,Object> req);
	void addJob(Integer regularMinute);
	void modifyJobTime(Integer regularMinute);
}
