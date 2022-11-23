package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.converter.ConversionUtils;

import java.util.List;

public class STR extends Cnd {
	public STR() {
		this.name = "s";
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			return ConversionUtils.convert(ls.get(1), String.class);
		} catch (Exception var4) {
			throw new RuntimeException(var4.getMessage());
		}
	}

	public String toString(List<?> ls, CndAnalysis processor) throws RuntimeException {
		return "'" + this.run(ls, processor) + "'";
	}
}
