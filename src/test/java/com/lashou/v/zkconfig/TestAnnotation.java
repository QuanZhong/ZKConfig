package com.lashou.v.zkconfig;

import java.lang.reflect.Field;

import com.lashou.v.zkconfig.annotaion.ZkConfig;

public class TestAnnotation {

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException{
		TestObj testObj = new TestObj();//实例化对象
		
		Class<?> c = testObj.getClass();
		
		//1、读取类配置
		boolean isAnnPres = c.isAnnotationPresent(ZkConfig.class);
		if(isAnnPres){
			ZkConfig zkconfig  = c.getAnnotation(ZkConfig.class);
			String hosts = zkconfig.hosts();
			int session = zkconfig.sessionTimeout();
			System.out.println(hosts+"="+session);
		}
		//2\读取属性配置
		Field[] fielsd = c.getDeclaredFields();
		for(Field field : fielsd){
			field.setAccessible(true);
			field.getType();
//			field.set(testObj, 1);
			System.out.println(field.getType()+",fieldName="+field.getName()+",filedValue="+field.get(testObj));
		}
		//3\读取方法配置
		
		System.out.println(Boolean.parseBoolean("true"));
	}
}
