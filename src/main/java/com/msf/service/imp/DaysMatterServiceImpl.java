package com.msf.service.imp;

import cn.hutool.core.bean.BeanUtil;
import com.msf.DaysMatterJDialog;
import com.msf.common.core.exception.BusinessException;
import com.msf.common.core.util.BeanUtils;
import com.msf.common.core.util.DateUtils;
import com.msf.common.core.util.VTools;
import com.msf.dao.IBaseDao;
import com.msf.data.DaysMatterClientBuilder;
import com.msf.data.dto.DaysMatterDTO;
import com.msf.data.entity.DaysMatter;
import com.msf.data.entity.DaysMatterConfig;
import com.msf.data.po.DaysMatterConfigPO;
import com.msf.data.vo.DaysMatterClientVO;
import com.msf.data.vo.DaysMatterConfigVO;
import com.msf.data.vo.PageVO;
import com.msf.schedule.config.ScheduleConfig;
import com.msf.service.IDaysMatterService;
import com.msf.service.IDicUtilService;
import com.msf.util.LunarSolarConverter;
import com.msf.util.cnd.CndAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @description 纪念日service
 * @author tianma
 * @date 2022/11/16 10:16
 **/
@Service
public class DaysMatterServiceImpl implements IDaysMatterService {
	@Autowired
	private DaysMatterJDialog daysMatterJDialog;
	@Autowired
	private IBaseDao dao;
	@Autowired
	private IDicUtilService dicUtilService;
	@Autowired
	private ScheduleConfig scheduleConfig;

	@Override
	public void call(Boolean openClient) {
		DaysMatterConfig daysMatterConfig = dao.doLoad("from DaysMatterConfig",null);
		if(daysMatterConfig == null){
			throw new BusinessException("请先配置客户端配置");
		}
		StringBuilder content = new StringBuilder("<html><body style=\"font-family:Microsoft YaHei\">");
		StringBuilder topContent = new StringBuilder();
		AtomicBoolean autoOpenClient = new AtomicBoolean(false);
		//存在小于等于thresholdDays天数的纪念日才打开客户端提示
		Integer thresholdDays = daysMatterConfig.getThresholdDays() == null ? 7 : daysMatterConfig.getThresholdDays();
		List<DaysMatter> list = dao.doQuery("from DaysMatter",null);
		List<DaysMatterDTO> daysMatterDTOS = list.stream().map(d -> {
			DaysMatterDTO daysMatterDTO = new DaysMatterDTO();
			BeanUtil.copyProperties(d,daysMatterDTO);
			return daysMatterDTO;
		}).collect(Collectors.toList());
		DaysMatterConfigPO daysMatterConfigPO = new DaysMatterConfigPO();
		BeanUtil.copyProperties(daysMatterConfig,daysMatterConfigPO);
		//设置客户端高宽
		daysMatterJDialog.setClientSize(daysMatterConfigPO.getClientWidth(),daysMatterConfigPO.getClientHeight());
		List<DaysMatterClientVO> daysMatterClientVOS = DaysMatterClientBuilder.instance(daysMatterDTOS,daysMatterConfigPO).build();
		if(!VTools.ListIsEmpty(daysMatterClientVOS)){
			daysMatterClientVOS = daysMatterClientVOS.stream().sorted(Comparator.comparing(DaysMatterClientVO::getMiniSortDays,(x, y)->{
				if(x < 0){
					if(y >= 0){
						return 1;
					}else {
						return y - x;
					}
				}else {
					if(y >= 0){
						return x - y;
					}else {
						return -1;
					}
				}
			})).collect(Collectors.toList());
			content.append("<html><body style=\"font-family:Microsoft YaHei\">");
			topContent.append("<html><body style=\"font-family:Microsoft YaHei;background-color:#fefefe;\">");
			daysMatterClientVOS.forEach(d -> {
				if(!autoOpenClient.get()){
					if(d.getDistanceDays() != null && d.getDistanceDays() > 0 && d.getDistanceDays() <= thresholdDays){
						autoOpenClient.set(true);
					}
					if(d.getDistanceAnniversaryDays() != null && d.getDistanceAnniversaryDays() > 0 && d.getDistanceAnniversaryDays() <= thresholdDays){
						autoOpenClient.set(true);
					}
					if(d.getDistanceHundredDays() != null && d.getDistanceHundredDays() > 0 && d.getDistanceHundredDays() <= thresholdDays){
						autoOpenClient.set(true);
					}
				}
				if(d.getTop() != null && d.getTop() == 1){
					topContent.append(d.getContent());
				}else {
					content.append(d.getContent());
				}
			});
			content.append("</body></html>");
			topContent.append("</body></html>");
			daysMatterJDialog.setTopContent(VTools.StringIsEmpty(topContent.toString())?" ": topContent.toString());
			daysMatterJDialog.setContent(content.toString());
			if(openClient || autoOpenClient.get()){
				daysMatterJDialog.doShow();
			}
		}
	}

	@Override
	public PageVO<Map<String,Object>> doQuery(Map<String, Object> req) {
		int pageSize = Integer.parseInt(req.get("pageSize")+"");
		int pageNo = Integer.parseInt(req.get("pageNo")+"");
		String schemaCode = req.get("schemaCode")+"";
		StringBuilder hql = new StringBuilder("from DaysMatter a ");
		if (req.containsKey("cnd") && null != req.get("cnd")) {
			List<Object> cnd = (List<Object>) req.get("cnd");
			String cndStr = CndAnalysis.instance().toString(cnd);
			if(cndStr.length()>0){
				hql.append(" where ");
				hql.append(cndStr);
			}
		}
		hql.append(" order by id desc");
		PageVO<DaysMatter> page = dao.doPage(hql.toString(),pageNo,pageSize,null);
		PageVO<Map<String,Object>> pageVO = new PageVO<>();
		pageVO.setTotalCount(page.getTotalCount());
		pageVO.setPageNo(pageNo);
		pageVO.setPageSize(pageSize);
		List<Map<String,Object>> list = new ArrayList<>();
		list.addAll(page.getData().stream().map(p -> BeanUtil.beanToMap(p)).collect(Collectors.toList()));
		dicUtilService.addDicText(schemaCode,list);
		list.forEach(l -> l.put("dateShow","2".equals(l.get("dateType")+"") ? LunarSolarConverter.lunarDateToGanZhi(l.get("date")+"") : l.get("date")));
		pageVO.setData(list);
		return pageVO;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class})
	public Map<String, Object> doSave(Map req) {
		if(!req.containsKey("data")){
			throw new BusinessException("保存数据不能为空!保存失败!");
		}
		if(!req.containsKey("op")){
			throw new BusinessException("op操作类型不能为空!保存失败!");
		}
		String op = req.get("op") +"";
		Map<String,Object> data = (HashMap<String, Object>)req.get("data");
		try{
			DaysMatter daysMatter = new DaysMatter();
			daysMatter = BeanUtil.mapToBean(data,daysMatter.getClass(),false);
			if(daysMatter.getTop() == 1){
				dao.executeHql("update DaysMatter set top = 0",null);
			}
			if("create".equals(op)){
				dao.save(daysMatter);
			}else if("update".equals(op)){
				dao.update(daysMatter);
			}
			this.call(true);
			Map<String, Object> res = new HashMap<>();
			res.put("body",BeanUtils.beanToMap(daysMatter));
			return res;
		} catch (Exception e){
			e.printStackTrace();
			throw new BusinessException(e.getMessage(),e);
		}
	}

	@Override
	public DaysMatterConfigVO getConfig() {
		DaysMatterConfig daysMatterConfig = dao.doLoad("from DaysMatterConfig",null);
		if(daysMatterConfig != null){
			DaysMatterConfigVO daysMatterConfigVO = new DaysMatterConfigVO();
			BeanUtil.copyProperties(daysMatterConfig,daysMatterConfigVO);
			return daysMatterConfigVO;
		}
		return null;
	}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConfig(DaysMatterConfigPO daysMatterConfigPO) {
        DaysMatterConfig daysMatterConfig = dao.doLoad("from DaysMatterConfig",null);
		daysMatterConfig.setRegularMinute(daysMatterConfigPO.getRegularMinute());
		daysMatterConfig.setThresholdDays(daysMatterConfigPO.getThresholdDays());
		daysMatterConfig.setClientWidth(daysMatterConfigPO.getClientWidth());
		daysMatterConfig.setClientHeight(daysMatterConfigPO.getClientHeight());
        dao.update(daysMatterConfig);
        setScheduleCron(daysMatterConfig.getRegularMinute());
        call(true);
    }

	@Override
	public String getLunarDate(Map<String,Object> req) {
		String date = req.get("date")+"";
		if(!VTools.StringIsEmpty(date)){
			return LunarSolarConverter.lunarDateToGanZhi(date);
		}
		return null;
	}

	private void setScheduleCron(Integer regularMinute) {
		scheduleConfig.setCron("0 0/"+regularMinute+" * * * ?");
	}
}
