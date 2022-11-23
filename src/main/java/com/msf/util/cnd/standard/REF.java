package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;

import java.util.List;

public class REF extends Cnd {
	public REF() {
		this.symbol = "$";
		this.name = this.symbol;
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			String nm = (String)ls.get(1);
//			if (nm.startsWith(":")) {
//				nm = nm.substring(4);
//			}

			return nm;
		} catch (Exception var4) {
			throw new RuntimeException(var4.getMessage());
		}
	}

	public String toString(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			String nm = (String)ls.get(1);
			if("@orgCode".equals(nm)){
				nm = "''";
			}else if("@username".equals(nm)){
				nm = "''";
			}else if("@deptId".equals(nm)){
				nm = "";
			}else if("@officeId".equals(nm)){
				nm = "";
			}
			return nm;
		} catch (Exception var8) {
			throw new RuntimeException(var8);
		}
	}
}
