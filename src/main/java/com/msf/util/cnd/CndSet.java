package com.msf.util.cnd;

import com.msf.util.cnd.standard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CndSet {
	private String name = "base";
	private HashMap<String, Cnd> cnds = new HashMap();

	public CndSet() {
		List<Cnd> cnd_list = new ArrayList<Cnd>();
		cnd_list.add(new AND());
		cnd_list.add(new CONCAT());
		cnd_list.add(new DEC());
		cnd_list.add(new DIV());
		cnd_list.add(new EQ());
		cnd_list.add(new GE());
		cnd_list.add(new GT());
		cnd_list.add(new IF());
		cnd_list.add(new IN());
		cnd_list.add(new NOTIN());
		cnd_list.add(new LE());
		cnd_list.add(new LEN());
		cnd_list.add(new LIKE());
		cnd_list.add(new LT());
		cnd_list.add(new MUL());
		cnd_list.add(new NE());
		cnd_list.add(new NUM());
		cnd_list.add(new OR());
		cnd_list.add(new PY());
		cnd_list.add(new REF());
		cnd_list.add(new STR());
		cnd_list.add(new SUBSTR());
		cnd_list.add(new SUM());
		cnd_list.add(new DATE());
		cnd_list.add(new ISNULL());
		cnd_list.add(new NOTNULL());
		cnd_list.add(new BETWEEN());
		cnd_list.add(new AGE());
		cnd_list.add(new TODATE());
		setCnds(cnd_list);
	}

	public void setName(String nm) {
		this.name = nm;
	}

	public String getName() {
		return this.name;
	}

	public void setCnds(List<Cnd> cnds) {
		Iterator i$ = cnds.iterator();

		while(i$.hasNext()) {
			Cnd cnd = (Cnd)i$.next();
			this.addCnd(cnd.getName(), cnd);
		}

	}

	public void addCnd(String nm, Cnd cnd) {
		this.cnds.put(nm, cnd);
	}

	public void register(String nm, Cnd cnd) {
		this.cnds.put(nm, cnd);
	}

	public Cnd getCnd(String nm) {
		return this.cnds.containsKey(nm) ? (Cnd)this.cnds.get(nm) : null;
	}
}
