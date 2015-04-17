package com.lashou.v.zkconfig.bean;

import java.io.Serializable;

/**
 * 配置属性元数据类
 * ClassName: ConfBeanMetaData <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date: 2015年4月15日 上午10:00:29 <br/>
 *
 * @author QuanZhong
 * @version 
 * @since JDK 1.6
 */
public class ConfBeanMetaData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2410106113334309756L;
	
	/**
	 * 配置文件key值
	 */
	private String key;
	/**
	 * 配置类,实体对象
	 */
	private Object instance;
	/**
	 * 配置类（实体对象）属性名
	 */
	private String fieldName;
	/**
	 * 配置类（实体对象）当前属性值
	 */
	private Object fieldValue;
	/**
	 * 配置类（实体对象）当前属性类型
	 */
	private Class<?> fieldType;
	
	
	public ConfBeanMetaData() {
		super();
	}
	
	public ConfBeanMetaData(Object instance,String key, String fieldName, Object fieldValue,
			Class<?> fieldType) {
		super();
		this.instance = instance;
		this.key = key;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.fieldType = fieldType;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Object getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}
	public Class<?> getFieldType() {
		return fieldType;
	}
	public void setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
	}

	public Object getInstance() {
		return instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}

	@Override
	public String toString() {
		return "ConfBeanMetaData [key=" + key + ", instance=" + instance
				+ ", fieldName=" + fieldName + ", fieldValue=" + fieldValue
				+ ", fieldType=" + fieldType + "]";
	}
	

}
