package com.msf.util.cnd;

import com.msf.common.core.util.JSONUtils;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CndAnalysis {
	private static final String BASE_LANG = "base";
	private static ConcurrentHashMap<String, CndSet> languages = new ConcurrentHashMap();
	private static ConcurrentHashMap<String, CndAnalysis> instances = new ConcurrentHashMap();
	private String language;
	private static List<CndSet> cndSets;

	public CndAnalysis() {
//		this("base");
//		addCndSet(new CndSet());
//		this.cndSets.add(new CndSet());
	}

	private CndAnalysis(String lang) {
		this.language = lang;
		instances.put(this.language, this);
	}

	public static CndAnalysis instance(String lang) throws RuntimeException {
		if (lang == null) {
			return instance();
		} else if (!languages.containsKey(lang)) {
			languages.put(lang, new CndSet());
			return instance(lang);
			//throw new RuntimeException("expr language[" + lang + "] is not found.");
		} else {
			CndAnalysis o = null;
			if (!instances.containsKey(lang)) {
				o = new CndAnalysis(lang);
			} else {
				o = (CndAnalysis)instances.get(lang);
			}

			return o;
		}
	}

	public static CndAnalysis instance() throws RuntimeException {
		return instance("base");
	}

	public void setCndSets(List<CndSet> langs) {
		Iterator i$ = langs.iterator();

		while(i$.hasNext()) {
			CndSet lang = (CndSet)i$.next();
			this.addCndSet(lang.getName(), lang);
		}

	}

	public void addCndSet(String nm, CndSet es) {
		languages.put(nm, es);
	}

	public void addCndSet(CndSet es) {
		this.addCndSet("base", es);
	}

	private Cnd getCnd(String nm) {
		Cnd expr = null;
		if (languages.containsKey(this.language)) {
			expr = ((CndSet)languages.get(this.language)).getCnd(nm);
		}

		if (expr == null) {
			expr = ((CndSet)languages.get("base")).getCnd(nm);
		}

		return expr;
	}

	private Cnd lookup(List<?> ls) throws RuntimeException {
		if (ls != null && !ls.isEmpty()) {
			String nm = (String)ls.get(0);
			Cnd expr = this.getCnd(nm);
			if (expr == null) {
				throw new RuntimeException("expr[" + nm + "] not found.");
			} else {
				return expr;
			}
		} else {
			throw new RuntimeException("expr list is empty.");
		}
	}

	private List<?> parseStr(String exp) throws RuntimeException {
		try {
			List<?> ls = (List) JSONUtils.parse(exp, List.class);
			return ls;
		} catch (Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public Object run(String exp) throws RuntimeException {
		return this.run(this.parseStr(exp));
	}

	public String toString(String exp) throws RuntimeException {
		return this.toString(this.parseStr(exp));
	}

	public Object run(List<?> ls) throws RuntimeException {
		return this.lookup(ls).run(ls, this);
	}

	public String toString(List<?> ls) throws RuntimeException {
		if(ls.size()==0){
			return "";
		}
		return this.lookup(ls).toString(ls, this);
	}

//	public String toString(List<?> ls, boolean forPreparedStatement) throws RuntimeException {
//		this.configExpressionContext(forPreparedStatement);
//		return this.toString(ls);
//	}
//
//	private void configExpressionContext(boolean forPreparedStatement) {
//		CndContextBean bean;
//		if (ContextUtils.hasKey("$exp")) {
//			bean = (CndContextBean)ContextUtils.get("$exp", CndContextBean.class);
//			bean.clearPatameters();
//		} else {
//			bean = new CndContextBean();
//			ContextUtils.put("$exp", bean);
//		}
//
//		bean.setForPreparedStatement(forPreparedStatement);
//	}
}
