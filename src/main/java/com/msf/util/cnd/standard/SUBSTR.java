package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.converter.ConversionUtils;

import java.util.List;

public class SUBSTR extends Cnd {
	public SUBSTR() {
		this.symbol = "substring";
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			String str = (String)processor.run((List)ls.get(1));
			int start = (Integer) ConversionUtils.convert(ls.get(2), Integer.class);
			if (ls.size() == 4) {
				int end = (Integer)ConversionUtils.convert(ls.get(3), Integer.class);
				return str.substring(start, end);
			} else {
				return str.substring(start);
			}
		} catch (Exception var6) {
			throw new RuntimeException(var6.getMessage());
		}
	}

	public String toString(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			String str = processor.toString((List)ls.get(1));
			int start = (Integer)ConversionUtils.convert(ls.get(2), Integer.class);
			StringBuffer sb = (new StringBuffer(this.symbol)).append("(").append(str).append(",").append(start);
			if (ls.size() == 4) {
				int end = (Integer) ConversionUtils.convert(ls.get(3), Integer.class);
				sb.append(",").append(end);
			}

			sb.append(")");
			return sb.toString();
		} catch (Exception var7) {
			throw new RuntimeException(var7.getMessage());
		}
	}
}
