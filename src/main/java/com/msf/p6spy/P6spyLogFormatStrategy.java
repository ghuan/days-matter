package com.msf.p6spy;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.apache.commons.lang.StringUtils;

/**
 * author:huan.gao
 * Date:2019/9/23
 * Time:11:12
 **/
public class P6spyLogFormatStrategy implements MessageFormattingStrategy {
	@Override
	public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
		return StringUtils.isNotEmpty(sql) && sql.toUpperCase().indexOf("SELECT 1")<0
			?
			"\n执行 SQL：" + sql.replaceAll("[\\s]+", " ")
				+"\n耗时：" + elapsed  + "ms\n "
			: "";
	}
}
