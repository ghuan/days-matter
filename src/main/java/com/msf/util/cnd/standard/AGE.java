package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.converter.ConversionUtils;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;

import java.sql.Date;
import java.util.List;

public class AGE extends Cnd {
	public AGE() {
		this.name = "age";
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			Object lso = ls.get(1);
			LocalDate birthLocal;
			if (lso instanceof String) {
				birthLocal = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate((String)lso);
			} else {
				if (!(lso instanceof List)) {
					throw new RuntimeException("arg[1] birthday invalid.");
				}

				Date birth = (Date) ConversionUtils.convert(processor.run((List)lso), Date.class);
				birthLocal = new LocalDate(birth);
			}

			LocalDate today = new LocalDate();
			Period period = new Period(birthLocal, today, PeriodType.yearMonthDay());
			return period.getYears();
		} catch (Exception var7) {
			throw new RuntimeException(var7.getMessage());
		}
	}

	public String toString(List<?> ls, CndAnalysis processor) throws RuntimeException {
		return "" + this.run(ls, processor);
	}
}
