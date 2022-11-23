package com.msf.jpa.transform;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyChainedImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by huan.gao on 2019/5/16.
 * aliasToBean 转换bean 跳过字段名匹配不上的
 */
public class MyAliasToBeanResultTransformer extends AliasedTupleSubsetResultTransformer {
	private final Class resultClass;
	private boolean isInitialized;
	private String[] aliases;
	private Setter[] setters;

	public MyAliasToBeanResultTransformer(Class resultClass) {
		if(resultClass == null) {
			throw new IllegalArgumentException("resultClass cannot be null");
		} else {
			this.isInitialized = false;
			this.resultClass = resultClass;
		}
	}

	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
		return false;
	}

	public Object transformTuple(Object[] tuple, String[] aliases) {
		try {
			if(!this.isInitialized) {
				this.initialize(aliases);
			} else {
				this.check(aliases);
			}

			Object result = this.resultClass.newInstance();

			for(int e = 0; e < aliases.length; ++e) {
				if(this.setters[e] != null) {
					try{
						if(tuple[e] != null){
							Class[] classes = this.setters[e].getMethod().getParameterTypes();
							Class fieldType = classes[0];
							Class objectType = tuple[e].getClass();
							//object类型 是 field的子类或同类
							if(fieldType.isAssignableFrom(objectType)){
								this.setters[e].set(result, tuple[e],(SessionFactoryImplementor)null);
							}else if(Date.class.isAssignableFrom(fieldType)){//Timestamp java.sql.Date extends java.util.Date
								Date tm = null;
								if(Long.class.isAssignableFrom(objectType)){//时间戳类型
									if(Timestamp.class.isAssignableFrom(fieldType)){
										tm = new Timestamp((Long) tuple[e]);
									}else if(java.sql.Date.class.isAssignableFrom(fieldType)){
										tm = new java.sql.Date((Long) tuple[e]);
									}else {
										tm = new Date((Long) tuple[e]);
									}
								}else if(String.class.isAssignableFrom(objectType)){//字符串格式化类型
									tuple[e] = ((String)tuple[e]).replaceAll("T"," ");
									Date tm1;
									try{
										tm1 = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)).parse((String) tuple[e]);
									}catch (Exception e1){
										tm1 = (new SimpleDateFormat("yyyy-MM-dd")).parse((String) tuple[e]);
									}
									if(Timestamp.class.isAssignableFrom(fieldType)){
										tm = new Timestamp(tm1.getTime());
									}else if(java.sql.Date.class.isAssignableFrom(fieldType)){
										tm = new java.sql.Date(tm1.getTime());
									}else {
										tm = tm1;
									}
								}
								this.setters[e].set(result, tm,(SessionFactoryImplementor)null);
							}else if(Number.class.isAssignableFrom(fieldType)){//field类型是数字包装类型
								if(Integer.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, Integer.parseInt(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(Double.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, Double.parseDouble(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(Long.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, Long.parseLong(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(Float.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, Float.parseFloat(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(Short.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, Short.parseShort(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(Byte.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, Byte.parseByte(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(BigDecimal.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, new BigDecimal(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(BigInteger.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, new BigInteger(tuple[e]+""),(SessionFactoryImplementor)null);
								}
							}else if(fieldType.isPrimitive()){//field类型是否为8个基本类型
								if(int.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, Integer.parseInt(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(double.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, Double.parseDouble(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(long.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, Long.parseLong(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(float.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, Float.parseFloat(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(short.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, Short.parseShort(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(byte.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, Byte.parseByte(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(boolean.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, Boolean.parseBoolean(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(char.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, tuple[e],(SessionFactoryImplementor)null);
								}
							} else if(CharSequence.class.isAssignableFrom(fieldType)){//field是字符串类型
								if(String.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, tuple[e]+"",(SessionFactoryImplementor)null);
								}else if(StringBuffer.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, new StringBuffer(tuple[e]+""),(SessionFactoryImplementor)null);
								}else if(StringBuilder.class.isAssignableFrom(fieldType)){
									this.setters[e].set(result, new StringBuilder(tuple[e]+""),(SessionFactoryImplementor)null);
								}
							}
						}else {
							this.setters[e].set(result, null,(SessionFactoryImplementor)null);
						}

					}catch (Exception ex){
						if(ex instanceof ClassCastException){//如果无法转换，则跳过
							continue;
						}
					}

				}
			}

			return result;
		} catch (InstantiationException var5) {
			throw new HibernateException("Could not instantiate resultclass: " + this.resultClass.getName());
		} catch (IllegalAccessException var6) {
			throw new HibernateException("Could not instantiate resultclass: " + this.resultClass.getName());
		}
	}

	private void initialize(String[] aliases) {
		PropertyAccessStrategyChainedImpl propertyAccessStrategy = new PropertyAccessStrategyChainedImpl(new PropertyAccessStrategy[]{PropertyAccessStrategyBasicImpl.INSTANCE, PropertyAccessStrategyFieldImpl.INSTANCE, PropertyAccessStrategyMapImpl.INSTANCE});
		this.aliases = new String[aliases.length];
		this.setters = new Setter[aliases.length];

		for(int i = 0; i < aliases.length; ++i) {
			String alias = aliases[i];
			if(alias != null) {
				try{
					this.aliases[i] = alias;
					this.setters[i] = propertyAccessStrategy.buildPropertyAccess(this.resultClass, alias).getSetter();
				}catch (Exception e){
					continue;
				}
			}
		}

		this.isInitialized = true;
	}

	private void check(String[] aliases) {
		if(!Arrays.equals(aliases, this.aliases)) {
			throw new IllegalStateException("aliases are different from what is cached; aliases=" + Arrays.asList(aliases) + " cached=" + Arrays.asList(this.aliases));
		}
	}

	public boolean equals(Object o) {
		if(this == o) {
			return true;
		} else if(o != null && this.getClass() == o.getClass()) {
			MyAliasToBeanResultTransformer that = (MyAliasToBeanResultTransformer)o;
			return !this.resultClass.equals(that.resultClass)?false:Arrays.equals(this.aliases, that.aliases);
		} else {
			return false;
		}
	}

	public int hashCode() {
		int result = this.resultClass.hashCode();
		result = 31 * result + (this.aliases != null?Arrays.hashCode(this.aliases):0);
		return result;
	}
}
