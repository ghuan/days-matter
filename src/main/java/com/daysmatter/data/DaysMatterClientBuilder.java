package com.daysmatter.data;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.daysmatter.data.dto.DaysMatterConfigDTO;
import com.daysmatter.data.dto.DaysMatterDTO;
import com.daysmatter.data.vo.DaysMatterClientVO;
import com.daysmatter.enums.RepeatDateStrategyEnum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tianma
 * @description 纪念日构建器
 * @date 2022/11/16 11:35
 **/
public class DaysMatterClientBuilder {
    private List<DaysMatterDTO> daysMatterDTOS;
    private DaysMatterConfigDTO daysMatterConfigPO;
    private Date now;

    private DaysMatterClientBuilder(List<DaysMatterDTO> daysMatterDTOS, DaysMatterConfigDTO daysMatterConfigPO) {
        this.daysMatterDTOS = daysMatterDTOS;
        this.daysMatterConfigPO = daysMatterConfigPO;
        now = DateUtil.parseDate(DateUtil.formatDate(new Date()));
    }

    public static DaysMatterClientBuilder instance(List<DaysMatterDTO> daysMatterDTOS, DaysMatterConfigDTO daysMatterConfigPO) {
        return new DaysMatterClientBuilder(daysMatterDTOS, daysMatterConfigPO);
    }

    /**
     * @description 构建DaysMatterClientVO
     * @author tianma
     * @date 2022/11/18 15:35
     **/
    public List<DaysMatterClientVO> build() {
        if (CollectionUtil.isNotEmpty(daysMatterDTOS)) {
            List<DaysMatterClientVO> daysMatterClientVOS = new ArrayList<>();
            daysMatterDTOS.forEach(d -> {
                boolean containBeginDate = d.getContainBeginDate() != null && d.getContainBeginDate() == 1;
                Date date = DateUtil.parseDate(DateUtil.formatDate(d.getDate()));
                DaysMatterClientVO daysMatterClientVO = new DaysMatterClientVO();
                daysMatterClientVO.setTop(d.getTop());
                int days = (int) ((date.getTime() - now.getTime()) / (1000 * 3600 * 24));
                if (days < 0 && d.getBigDay() != null && d.getBigDay() == 1) {
                    //周年和百日提醒
                    Date anniversaryStart = date;
                    Date HundredStart = date;
                    Integer distanceAnniversaryDays;
                    Integer anniversaryNum = 0;
                    Integer distanceHundredDays;
                    Integer hundredNum = 0;

                    while (true) {
                        anniversaryNum++;
                        anniversaryStart = RepeatDateStrategyEnum.REPEAT_YEAR.getDateOnce(anniversaryStart, d.getDateType() == 2);
                        distanceAnniversaryDays = (int) ((anniversaryStart.getTime() - now.getTime()) / (1000 * 3600 * 24));
                        if (distanceAnniversaryDays >= 0) {
                            daysMatterClientVO.setAnniversaryDate(DateUtil.formatDate(anniversaryStart));
                            break;
                        }
                    }
                    while (true) {
                        hundredNum++;
                        //起始日算一天
                        HundredStart = DateUtil.offsetDay(HundredStart, (hundredNum == 1 && containBeginDate) ? 99 : 100);
                        distanceHundredDays = (int) ((HundredStart.getTime() - now.getTime()) / (1000 * 3600 * 24));
                        if (distanceHundredDays >= 0) {
                            daysMatterClientVO.setHundredDate(DateUtil.formatDate(HundredStart));
                            break;
                        }
                    }
                    daysMatterClientVO.setDistanceAnniversaryDays(distanceAnniversaryDays);
                    daysMatterClientVO.setAnniversaryNum(anniversaryNum);
                    daysMatterClientVO.setDistanceHundredDays(distanceHundredDays);
                    daysMatterClientVO.setHundredNum(hundredNum);
                }
                if (days < 0 && d.getRepeat() != null && d.getRepeat() > 0) {
                    Date start = DateUtil.parseDate(DateUtil.formatDate(d.getDate()));
                    //重复
                    while (true) {
                        start = RepeatDateStrategyEnum.getByRepeatType(d.getRepeat()).getDateOnce(start, d.getDateType() == 2);
                        days = (int) ((start.getTime() - now.getTime()) / (1000 * 3600 * 24));
                        if (days >= 0) {
                            daysMatterClientVO.setDate(start);
                            break;
                        }
                    }
                }
                daysMatterClientVO.setDistanceDays(days < 0 && containBeginDate ? days - 1 : days);
                buildContent(d, daysMatterClientVO);
                daysMatterClientVOS.add(daysMatterClientVO);
            });
            return daysMatterClientVOS;
        }
        return null;
    }

    private String dayNumColor(int distanceDays) {
        return distanceDays >= 0
                ? (distanceDays > 15 ? "#2aabd2"
                : (distanceDays > 7 ? "#337ab7"
                : (distanceDays > 3 ? "#eb9316"
                : "#d9534f"))) : "#2aabd2";
    }

    /**
     * @description 构建展示内容
     * @author tianma
     * @date 2022/11/18 15:28
     **/
    private void buildContent(DaysMatterDTO daysMatterDTO, DaysMatterClientVO daysMatterClientVO) {
        String normalFontSize = "12px";
        String dayFontSize = "18px";
        if (daysMatterDTO.getTop() != null && daysMatterDTO.getTop() == 1) {
            normalFontSize = "15px";
            dayFontSize = "20px";
        }
        StringBuilder content = new StringBuilder();
        content.append("<table width=\"").append(this.daysMatterConfigPO.getClientWidth()).append("\" style=\"table-layout:fixed;")
                .append((daysMatterDTO.getTop() != null && daysMatterDTO.getTop() == 1) ? "background-color:#fefefe;" : "border-bottom:1px dashed #2aabd2;").append("font-size:").append(normalFontSize).append("\">");
        content.append("<tr><td style=\"text-align:").append((daysMatterDTO.getTop() != null && daysMatterDTO.getTop() == 1) ? "center" : "left").append("\">");
        content.append(daysMatterDTO.getTop() != null && daysMatterDTO.getTop() == 1 ? "" : "&emsp;");
        content.append("<span style=\"color:#337ab7;\">").append(daysMatterDTO.getTitle()).append("</span>")
                .append("<span style=\"color:#afb0b0;font-size:10px;\">(").append(DateUtil.formatDate(daysMatterDTO.getDate()).replaceAll("-", ""));
        if (daysMatterClientVO.getDistanceDays() != null && daysMatterClientVO.getDate() != null) {
            content.append("-").append(DateUtil.formatDate(daysMatterClientVO.getDate()).replaceAll("-", ""));
        }
        content.append(")</span>");
        if (daysMatterClientVO.getDistanceDays() != null) {
            if (daysMatterClientVO.getDistanceDays() >= 0) {
                String color = dayNumColor(daysMatterClientVO.getDistanceDays());
                if (daysMatterClientVO.getDistanceDays() == 0) {
                    content.append("&emsp;<span style=\"color:").append(color).append(";font-size:").append(dayFontSize).append(";\">就是今天！</span>");
                } else {
                    content.append("&emsp;还有&emsp;<span style=\"color:").append(color).append(";font-size:").append(dayFontSize).append(";\">")
                            .append(daysMatterClientVO.getDistanceDays()).append("</span>&emsp;天");
                }
            } else {
                content.append("&emsp;已经&emsp;<span style=\"font-size:").append(dayFontSize).append(";\">")
                        .append((-daysMatterClientVO.getDistanceDays())).append("</span>&emsp;天");
            }
        }
        content.append("</td></tr>");
        //周年
        if (daysMatterClientVO.getDistanceAnniversaryDays() != null) {
            if (daysMatterClientVO.getDistanceAnniversaryDays() >= 0) {
                content.append("<tr><td style=\"text-align:").append((daysMatterDTO.getTop() != null && daysMatterDTO.getTop() == 1) ? "center" : "left").append("\">");
                content.append("&emsp;<span style=\"color:#337ab7;\">").append(daysMatterClientVO.getAnniversaryNum())
                        .append("周年").append("</span>").append("<span style=\"color:#afb0b0;font-size:10px;\">(").append(daysMatterClientVO.getAnniversaryDate().replaceAll("-", "")).append(")</span>");
                String color = dayNumColor(daysMatterClientVO.getDistanceAnniversaryDays());
                if (daysMatterClientVO.getDistanceAnniversaryDays() == 0) {
                    content.append("&emsp;<span style=\"color:").append(color).append(";font-size:").append(dayFontSize).append(";\">")
                            .append("就是今天！</span>");
                } else {
                    content.append("&emsp;还有&emsp;<span style=\"color:").append(color).append(";font-size:").append(dayFontSize).append(";\">")
                            .append(daysMatterClientVO.getDistanceAnniversaryDays()).append("</span>&emsp;天");
                }
                content.append("</td></tr>");
            }
        }
        //百日
        if (daysMatterClientVO.getDistanceHundredDays() != null) {
            if (daysMatterClientVO.getDistanceHundredDays() >= 0) {
                content.append("<tr><td style=\"text-align:").append((daysMatterDTO.getTop() != null && daysMatterDTO.getTop() == 1) ? "center" : "left").append("\">");
                content.append("&emsp;<span style=\"color:#337ab7;\">").append(daysMatterClientVO.getHundredNum() * 100)
                        .append("日").append("</span>").append("<span style=\"color:#afb0b0;font-size:10px;\">(").append(daysMatterClientVO.getHundredDate().replaceAll("-", "")).append(")</span>");
                String color = dayNumColor(daysMatterClientVO.getDistanceHundredDays());
                if (daysMatterClientVO.getDistanceHundredDays() == 0) {
                    content.append("&emsp;<span style=\"color:").append(color).append(";font-size:").append(dayFontSize).append(";\">")
                            .append("就是今天！</span>");
                } else {
                    content.append("&emsp;还有&emsp;<span style=\"color:").append(color).append(";font-size:").append(dayFontSize).append(";\">")
                            .append(daysMatterClientVO.getDistanceHundredDays()).append("</span>&emsp;天");
                }
                content.append("</td></tr>");
            }
        }
        content.append("</table>");
        daysMatterClientVO.setContent(content.toString());
    }
}
