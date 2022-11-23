package com.msf.jpa.hibernate.dialect;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

public class MSFMySQL5Dialect extends MySQL5Dialect {
	public MSFMySQL5Dialect(){
		super();
		registerHibernateType(Types.CHAR, StandardBasicTypes.STRING.getName());
		registerHibernateType(Types.NULL, StandardBasicTypes.STRING.getName());
		registerHibernateType(Types.BIGINT, StandardBasicTypes.LONG.getName());
	}
}
