package com.daysmatter.service;

import com.daysmatter.data.dto.DaysMatterConfigDTO;
import com.daysmatter.data.dto.DaysMatterDTO;
import com.daysmatter.data.vo.DaysMatterConfigVO;
import com.daysmatter.data.vo.DaysMatterVO;
import com.gh.framework.common.web.data.PageVO;

import java.util.List;
import java.util.Map;

/**
 *
* description：纪念日接口
 */
public interface IDaysMatterService {
	void call(Boolean openClient);
	PageVO<DaysMatterVO> getPage(DaysMatterDTO daysMatterDTO);
	Boolean delete(List<Long> ids);
	DaysMatterVO save(DaysMatterDTO daysMatterDTO);
	DaysMatterConfigVO getConfig();
	void saveConfig(DaysMatterConfigDTO daysMatterConfigDTO);
	String getLunarDate(String date);
}
