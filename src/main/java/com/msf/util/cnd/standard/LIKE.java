package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.converter.ConversionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

public class LIKE extends Cnd {
	public LIKE() {
		this.symbol = "like";
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			Object lso = ls.get(1);
			String str1 = null;
			if (lso instanceof List) {
				str1 = (String) ConversionUtils.convert(processor.run((List)lso), String.class);
			} else {
				str1 = (String)ConversionUtils.convert(lso, String.class);
			}

			lso = ls.get(2);
			String str2 = null;
			if (lso instanceof List) {
				str2 = (String) ConversionUtils.convert(processor.run((List)lso), String.class);
			} else {
				str2 = (String)ConversionUtils.convert(lso, String.class);
			}

			if (!StringUtils.contains(str2, "%")) {
				str2 = str2 + "%";
			}

			Pattern pattern = Pattern.compile(str2.replaceAll("%", ".*"));
			return pattern.matcher(str1).find();
		} catch (Exception var7) {
			throw new RuntimeException(var7.getMessage());
		}
	}

//	public String toString(List<?> ls, CndAnalysis processor) throws RuntimeException {
//		StringBuffer sb = new StringBuffer();
//		Object lso = ls.get(1);
//		String str1 = null;
//		if (lso instanceof List) {
//			str1 = processor.toString((List)lso);
//		} else {
//			str1 = (String)ConversionUtils.convert(lso, String.class);
//		}
//
//		sb.append(str1).append(" ").append(this.symbol).append(" ");
//		lso = ls.get(2);
//		String str2 = null;
//		if (lso instanceof List) {
//			str2 = processor.toString((List)lso);
//			Context ctx = ContextUtils.getContext();
//			Boolean forPreparedStatement = (Boolean)ctx.get("$exp.forPreparedStatement", Boolean.class);
//			if (forPreparedStatement != null && forPreparedStatement && str2.startsWith(":")) {
//				sb.append(str2);
//				HashMap<String, Object> parameters = (HashMap)ctx.get("$exp.statementParameters", HashMap.class);
//				String key = str2.substring(1);
//				String val = (String)ConversionUtils.convert(parameters.get(key), String.class);
//				if (!StringUtils.endsWith(val, "%")) {
//					parameters.put(key, val + "%");
//				}
//			} else {
//				if (!str2.startsWith("'")) {
//					sb.append("'");
//				}
//
//				if (str2.endsWith("'")) {
//					str2 = str2.substring(0, str2.length() - 1);
//				}
//
//				sb.append(str2);
//				if (!StringUtils.contains(str2, "%")) {
//					sb.append("%");
//				}
//
//				sb.append("'");
//			}
//		} else {
//			str2 = (String)ConversionUtils.convert(lso, String.class);
//			sb.append("'").append(str2);
//			if (!StringUtils.contains(str2, "%")) {
//				sb.append("%");
//			}
//
//			sb.append("'");
//		}
//
//		return sb.toString();
//	}
}
