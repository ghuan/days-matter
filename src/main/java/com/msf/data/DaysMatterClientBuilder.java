package com.msf.data;

import com.msf.common.core.util.DateUtils;
import com.msf.common.core.util.VTools;
import com.msf.data.dto.DaysMatterDTO;
import com.msf.data.po.DaysMatterConfigPO;
import com.msf.data.vo.DaysMatterClientVO;
import com.msf.enums.RepeatDateStrategyEnum;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 纪念日构建器
 * @author tianma
 * @date 2022/11/16 11:35
 **/
public class DaysMatterClientBuilder {
	private List<DaysMatterDTO> daysMatterDTOS;
	private DaysMatterConfigPO daysMatterConfigPO;
	private Date now;
	private DaysMatterClientBuilder(List<DaysMatterDTO> daysMatterDTOS,DaysMatterConfigPO daysMatterConfigPO){
		this.daysMatterDTOS = daysMatterDTOS;
		this.daysMatterConfigPO = daysMatterConfigPO;
		now = DateUtils.parseDate(DateUtils.formatDate(new Date()));
	}
	public static DaysMatterClientBuilder instance(List<DaysMatterDTO> daysMatterDTOS,DaysMatterConfigPO daysMatterConfigPO){
		return new DaysMatterClientBuilder(daysMatterDTOS,daysMatterConfigPO);
	}

	/**
	 * @description 构建DaysMatterClientVO
	 * @author tianma
	 * @date 2022/11/18 15:35
	 **/
	public List<DaysMatterClientVO> build(){
		if(!VTools.ListIsEmpty(daysMatterDTOS)){
			List<DaysMatterClientVO> daysMatterClientVOS = new ArrayList<>();
			daysMatterDTOS.forEach(d -> {
				boolean containBeginDate = d.getContainBeginDate() != null && d.getContainBeginDate() == 1;
				DaysMatterClientVO daysMatterClientVO = new DaysMatterClientVO();
				daysMatterClientVO.setTop(d.getTop());
				int days = (int) ((DateUtils.parseDate(d.getDate()).getTime() - now.getTime()) / (1000*3600*24));
				if(days < 0 && d.getBigDay() != null && d.getBigDay() == 1){
					//周年和百日提醒
					Date anniversaryStart = DateUtils.parseDate(d.getDate());
					Date HundredStart = DateUtils.parseDate(d.getDate());
					Integer distanceAnniversaryDays;
					Integer anniversaryNum = 0;
					Integer distanceHundredDays;
					Integer hundredNum = 0;

					B: while(true){
						anniversaryNum++;
						anniversaryStart = RepeatDateStrategyEnum.REPEAT_YEAR.getDateOnce(anniversaryStart,d.getDateType() == 2);
						distanceAnniversaryDays = (int) ((anniversaryStart.getTime() - now.getTime()) / (1000*3600*24));
						if(distanceAnniversaryDays >= 0){
							daysMatterClientVO.setAnniversaryDate(DateUtils.formatDate(anniversaryStart));
							break B;
						}
					}
					C: while(true){
						hundredNum++;
						//起始日算一天
						HundredStart = DateUtils.addDays(HundredStart,(hundredNum == 1 && containBeginDate) ? 99 : 100);
						distanceHundredDays = (int) ((HundredStart.getTime() - now.getTime()) / (1000*3600*24));
						if(distanceHundredDays >= 0){
							daysMatterClientVO.setHundredDate(DateUtils.formatDate(HundredStart));
							break C;
						}
					}
					daysMatterClientVO.setDistanceAnniversaryDays(distanceAnniversaryDays);
					daysMatterClientVO.setAnniversaryNum(anniversaryNum);
					daysMatterClientVO.setDistanceHundredDays(distanceHundredDays);
					daysMatterClientVO.setHundredNum(hundredNum);
				}
				if(days < 0 && d.getRepeat() != null && d.getRepeat() > 0){
					Date start = DateUtils.parseDate(d.getDate());
					//重复
					B: while(true){
						start = RepeatDateStrategyEnum.getByRepeatType(d.getRepeat()).getDateOnce(start,d.getDateType() == 2);
						days = (int) ((start.getTime() - now.getTime()) / (1000*3600*24));
						if(days >= 0){
							daysMatterClientVO.setDate(DateUtils.formatDate(start));
							break B;
						}
					}
				}
				daysMatterClientVO.setDistanceDays(days<0&&containBeginDate?days-1:days);
				buildContent(d,daysMatterClientVO);
				daysMatterClientVOS.add(daysMatterClientVO);
			});
			return daysMatterClientVOS;
		}
		return null;
	}
	private String dayNumColor(int distanceDays){
		return distanceDays >=0
				? (distanceDays >=this.daysMatterConfigPO.getThresholdDays() ? "#2aabd2"
				: (distanceDays >=this.daysMatterConfigPO.getThresholdDays()/2 ? "#337ab7"
				: (distanceDays >=this.daysMatterConfigPO.getThresholdDays()/5 ? "#eb9316"
				: (distanceDays ==0 ? "#d9534f" : "" ) ) ) ) : "#2aabd2";
	}
	/**
	 * @description 构建展示内容
	 * @author tianma
	 * @date 2022/11/18 15:28
	 **/
	private void buildContent(DaysMatterDTO daysMatterDTO,DaysMatterClientVO daysMatterClientVO){
		String normalFontSize = "12px";
		String dayFontSize = "18px";
		if(daysMatterDTO.getTop() != null && daysMatterDTO.getTop() == 1){
			normalFontSize = "15px";
			dayFontSize = "20px";
		}
		StringBuilder content = new StringBuilder();
		content.append("<table width=\"").append(this.daysMatterConfigPO.getClientWidth()).append("\" style=\"table-layout:fixed;")
				.append((daysMatterDTO.getTop() != null && daysMatterDTO.getTop() == 1)?"background-color:#fefefe;":"border-bottom:1px dashed #2aabd2;").append("font-size:").append(normalFontSize).append("\">");
		content.append("<tr><td style=\"text-align:").append((daysMatterDTO.getTop() != null && daysMatterDTO.getTop() == 1)?"center":"left").append("\">");
		content.append(daysMatterDTO.getTop()!= null && daysMatterDTO.getTop() == 1 ? "" : "&emsp;");
		content.append("<span style=\"color:#337ab7;\">").append(daysMatterDTO.getTitle()).append("</span>")
		.append("<span style=\"color:#afb0b0;font-size:10px;\">(").append(daysMatterDTO.getDate().replaceAll("-",""));
		if(daysMatterClientVO.getDistanceDays() != null && daysMatterClientVO.getDate() != null){
			content.append("-").append(daysMatterClientVO.getDate().replaceAll("-",""));
		}
		content.append(")</span>");
		if(daysMatterClientVO.getDistanceDays() != null){
			if(daysMatterClientVO.getDistanceDays() >=0){
				String color = dayNumColor(daysMatterClientVO.getDistanceDays());
				if(daysMatterClientVO.getDistanceDays() == 0){
					content.append("&emsp;<span style=\"color:").append(color).append(";font-size:").append(dayFontSize).append(";\">就是今天！</span>");
				}else {
					content.append("&emsp;还有&emsp;<span style=\"color:").append(color).append(";font-size:").append(dayFontSize).append(";\">")
							.append(daysMatterClientVO.getDistanceDays()).append("</span>&emsp;天");
				}
			}else {
				content.append("&emsp;已经&emsp;<span style=\"font-size:").append(dayFontSize).append(";\">")
				.append((-daysMatterClientVO.getDistanceDays())).append("</span>&emsp;天");
			}
		}
		content.append("</td></tr>");
		//周年
		if(daysMatterClientVO.getDistanceAnniversaryDays() != null){
			if(daysMatterClientVO.getDistanceAnniversaryDays() >=0){
				content.append("<tr><td style=\"text-align:").append((daysMatterDTO.getTop() != null && daysMatterDTO.getTop() == 1)?"center":"left").append("\">");
				content.append("&emsp;<span style=\"color:#337ab7;\">").append(daysMatterClientVO.getAnniversaryNum())
						.append("周年").append("</span>").append("<span style=\"color:#afb0b0;font-size:10px;\">(").append(daysMatterClientVO.getAnniversaryDate().replaceAll("-","")).append(")</span>");
				String color = dayNumColor(daysMatterClientVO.getDistanceAnniversaryDays());
				if(daysMatterClientVO.getDistanceAnniversaryDays() == 0){
					content.append("&emsp;<span style=\"color:").append(color).append(";font-size:").append(dayFontSize).append(";\">")
					.append("就是今天！</span>");
				}else {
					content.append("&emsp;还有&emsp;<span style=\"color:").append(color).append(";font-size:").append(dayFontSize).append(";\">")
							.append(daysMatterClientVO.getDistanceAnniversaryDays()).append("</span>&emsp;天");
				}
				content.append("</td></tr>");
			}
		}
		//百日
		if(daysMatterClientVO.getDistanceHundredDays() != null){
			if(daysMatterClientVO.getDistanceHundredDays() >=0){
				content.append("<tr><td style=\"text-align:").append((daysMatterDTO.getTop() != null && daysMatterDTO.getTop() == 1)?"center":"left").append("\">");
				content.append("&emsp;<span style=\"color:#337ab7;\">").append(daysMatterClientVO.getHundredNum()*100)
						.append("日").append("</span>").append("<span style=\"color:#afb0b0;font-size:10px;\">(").append(daysMatterClientVO.getHundredDate().replaceAll("-","")).append(")</span>");
				String color = dayNumColor(daysMatterClientVO.getDistanceHundredDays());
				if(daysMatterClientVO.getDistanceHundredDays() == 0){
					content.append("&emsp;<span style=\"color:").append(color).append(";font-size:").append(dayFontSize).append(";\">")
							.append("就是今天！</span>");
				}else {
					content.append("&emsp;还有&emsp;<span style=\"color:").append(color).append(";font-size:").append(dayFontSize).append(";\">")
							.append(daysMatterClientVO.getDistanceHundredDays()).append("</span>&emsp;天");
				}
				content.append("</td></tr>");
			}
		}
		content.append("</table>");
		daysMatterClientVO.setContent(content.toString());
	}

	public static void main(String[] args) {
		System.out.println(DateUtils.formatDate(DateUtils.addDays(DateUtils.parseDate("2023-03-14"),999)));
		System.out.println((DateUtils.parseDate("2023-03-14").getTime() - DateUtils.parseDate("2020-03-14").getTime())/ (1000*3600*24));
	}
}
