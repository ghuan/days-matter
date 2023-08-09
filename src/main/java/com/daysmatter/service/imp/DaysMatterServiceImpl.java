package com.daysmatter.service.imp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.daysmatter.DaysMatterJDialog;
import com.daysmatter.data.DaysMatterClientBuilder;
import com.daysmatter.data.dto.DaysMatterConfigDTO;
import com.daysmatter.data.dto.DaysMatterDTO;
import com.daysmatter.data.entity.DaysMatter;
import com.daysmatter.data.entity.DaysMatterConfig;
import com.daysmatter.data.vo.DaysMatterClientVO;
import com.daysmatter.data.vo.DaysMatterConfigVO;
import com.daysmatter.data.vo.DaysMatterVO;
import com.daysmatter.repository.DaysMatterConfigRepository;
import com.daysmatter.repository.DaysMatterRepository;
import com.daysmatter.service.IDaysMatterService;
import com.daysmatter.util.LunarSolarConverter;
import com.gh.framework.common.core.enums.NumberEnum;
import com.gh.framework.common.web.data.PageVO;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @description 纪念日service
 * @author tianma
 * @date 2022/11/16 10:16
 **/
@Service
@SuppressWarnings("all")
public class DaysMatterServiceImpl implements IDaysMatterService {
	@Resource
	private DaysMatterJDialog daysMatterJDialog;

	@Resource
	private DaysMatterRepository daysMatterRepository;

	@Resource
	private DaysMatterConfigRepository daysMatterConfigRepository;

	@Override
	public void call(Boolean openClient) {
		DaysMatterConfigVO daysMatterConfig = getConfig();
		StringBuilder content = new StringBuilder("<html><body style=\"font-family:Microsoft YaHei\">");
		StringBuilder topContent = new StringBuilder();
		AtomicBoolean autoOpenClient = new AtomicBoolean(false);
		//存在小于等于thresholdDays天数的纪念日才打开客户端提示
		int thresholdDays = daysMatterConfig.getThresholdDays() == null ? 7 : daysMatterConfig.getThresholdDays();
		List<DaysMatter> list = daysMatterRepository.findAll();
		List<DaysMatterDTO> daysMatterDTOS = list.stream().map(d -> {
			DaysMatterDTO daysMatterDTO = new DaysMatterDTO();
			BeanUtil.copyProperties(d,daysMatterDTO);
			return daysMatterDTO;
		}).collect(Collectors.toList());
		DaysMatterConfigDTO daysMatterConfigDTO = new DaysMatterConfigDTO();
		BeanUtil.copyProperties(daysMatterConfig,daysMatterConfigDTO);
		//设置客户端高宽
		daysMatterJDialog.setClientSize(daysMatterConfigDTO.getClientWidth(),daysMatterConfigDTO.getClientHeight());
		List<DaysMatterClientVO> daysMatterClientVOS = DaysMatterClientBuilder.instance(daysMatterDTOS,daysMatterConfigDTO).build();
			if(CollectionUtil.isNotEmpty(daysMatterClientVOS)){
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
			daysMatterJDialog.setTopContent(Convert.toStr(topContent.toString()," "));
			daysMatterJDialog.setContent(content.toString());
			if(openClient || autoOpenClient.get()){
				daysMatterJDialog.doShow();
			}
		}
	}

	@Override
	public PageVO<DaysMatterVO> getPage(DaysMatterDTO daysMatterDTO) {
		Pageable pageable = PageRequest.of(daysMatterDTO.getPageNum()-1, daysMatterDTO.getPageSize(), Sort.Direction.DESC, "id");
		//组装查询条件
		Specification specification = (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = Lists.newArrayList();
			if(StrUtil.isNotEmpty(daysMatterDTO.getTitle())){
				predicates.add(criteriaBuilder.like(root.get("title"),"%"+daysMatterDTO.getTitle()+"%"));
			}
			if(daysMatterDTO.getDateType() != null){
				predicates.add(criteriaBuilder.equal(root.get("dateType"),daysMatterDTO.getDateType()));
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
        Page<DaysMatter> page = daysMatterRepository.findAll(specification,pageable);
		List<DaysMatterVO> list = BeanUtil.copyToList(page.getContent(),DaysMatterVO.class);
		list.forEach(l -> l.setDateShow(NumberEnum.TWO.getKey() == l.getDateType() ? LunarSolarConverter.lunarDateToGanZhi(DateUtil.formatDate(l.getDate())) : DateUtil.formatDate(l.getDate())));
		PageVO<DaysMatterVO> pageVO = new PageVO<>(daysMatterDTO.getPageNum(), daysMatterDTO.getPageSize(),list);
		return pageVO;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class})
	public Boolean delete(List<Long> ids) {
		daysMatterRepository.deleteAllById(ids);
		return true;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class})
	public DaysMatterVO save(DaysMatterDTO daysMatterDTO) {
		DaysMatter daysMatter = BeanUtil.copyProperties(daysMatterDTO,DaysMatter.class);
		daysMatterRepository.save(daysMatter);
		return BeanUtil.copyProperties(daysMatter,DaysMatterVO.class);
	}
	@Override
	@Transactional(rollbackFor = {Exception.class})
	public DaysMatterConfigVO getConfig() {
		List<DaysMatterConfig> daysMatterConfigs = daysMatterConfigRepository.findAll();
		DaysMatterConfig daysMatterConfig = null;
		if(CollectionUtil.isEmpty(daysMatterConfigs)){
			daysMatterConfig = new DaysMatterConfig();
			daysMatterConfig.setRegularMinute(180);
			daysMatterConfig.setThresholdDays(12);
			daysMatterConfig.setClientWidth(500);
			daysMatterConfig.setClientHeight(400);
			daysMatterConfigRepository.save(daysMatterConfig);
		}else {
			daysMatterConfig = daysMatterConfigs.get(0);
		}
		DaysMatterConfigVO daysMatterConfigVO = new DaysMatterConfigVO();
		BeanUtil.copyProperties(daysMatterConfig,daysMatterConfigVO);
		return daysMatterConfigVO;
	}
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConfig(DaysMatterConfigDTO daysMatterConfigDTO) {
        DaysMatterConfig daysMatterConfig = daysMatterConfigRepository.findAll().get(0);
		BeanUtil.copyProperties(daysMatterConfigDTO,daysMatterConfig);
		daysMatterConfigRepository.save(daysMatterConfig);
        call(true);
    }
	@Override
	public String getLunarDate(String date) {
		if(StrUtil.isNotEmpty(date)){
			return LunarSolarConverter.lunarDateToGanZhi(date);
		}
		return null;
	}

}
