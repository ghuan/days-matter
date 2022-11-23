package com.msf.p6spy;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.Slf4JLogger;
import org.apache.commons.lang.StringUtils;

public class P6SpySlf4JLogger extends Slf4JLogger {
	@Override
	public void logSQL(int connectionId, String now, long elapsed, Category category, String prepared, String sql, String url) {
		if(StringUtils.isNotEmpty(sql) && sql.toUpperCase().indexOf("SELECT 1") < 0){
			super.logSQL(connectionId, now, elapsed, category, prepared, sql, url);
		}
	}
}
