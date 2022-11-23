package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import org.springframework.util.ObjectUtils;

import java.util.List;

public class NE extends Cnd {
	public NE() {
		this.symbol = "!=";
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			Object v1 = ls.get(1);
			if (v1 instanceof List) {
				v1 = processor.run((List)v1);
			}

			int i = 2;

			for(int size = ls.size(); i < size; ++i) {
				Object v2 = ls.get(i);
				if (v2 instanceof List) {
					v2 = processor.run((List)v2);
				}

				if (ObjectUtils.nullSafeEquals(v1, v2)) {
					return false;
				}
			}

			return true;
		} catch (Exception var7) {
			throw new RuntimeException(var7.getMessage());
		}
	}
}
