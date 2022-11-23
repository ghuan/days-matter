package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.converter.ConversionUtils;

import java.util.List;

public class IF extends Cnd {
	public IF() {
		this.symbol = "if";
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			boolean status = (Boolean)processor.run((List)ls.get(1));
			Object result = null;
			if (status) {
				result = ls.get(2);
				if (result instanceof List) {
					result = processor.run((List)result);
				}
			} else {
				result = ls.get(3);
				if (result instanceof List) {
					result = processor.run((List)result);
				}
			}

			return result;
		} catch (Exception var5) {
			throw new RuntimeException(var5.getMessage());
		}
	}

	public String toString(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			StringBuffer sb = new StringBuffer(processor.toString((List)ls.get(1)));
			sb.append(" ? ");
			Object lso = ls.get(2);
			if (lso instanceof List) {
				sb.append(processor.toString((List)lso));
			} else {
				sb.append((String) ConversionUtils.convert(lso, String.class));
			}

			sb.append(" : ");
			lso = ls.get(3);
			if (lso instanceof List) {
				sb.append(processor.toString((List)ls.get(3)));
			} else {
				sb.append((String)ConversionUtils.convert(lso, String.class));
			}

			return sb.toString();
		} catch (Exception var5) {
			throw new RuntimeException(var5.getMessage());
		}
	}
}
