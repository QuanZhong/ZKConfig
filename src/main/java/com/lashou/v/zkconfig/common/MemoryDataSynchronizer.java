package com.lashou.v.zkconfig.common;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.lashou.v.zkconfig.bean.ConfBeanMetaData;

/**
 * 内存数据同步器，用于同步zk服务器数据变更到内存数据
 * @author QuanZhong
 * @version 
 * @since JDK 1.6
 */
public class MemoryDataSynchronizer {
	static Logger logger = Logger.getLogger(MemoryDataSynchronizer.class);
	
	public static int synchronize(byte[] data){
		try {
			Map<String,ConfBeanMetaData> elements = ElementCollection.getElementCollection();
			Properties prop = ZkConfigParser.parserData(data);
			if(prop != null){
				Set<Object> set = prop.keySet();
				for(Object obj : set){
					String value = prop.getProperty((String) obj);
					ConfBeanMetaData cbmd = elements.get((String) obj);
					if(cbmd != null && !value.equals(cbmd.getFieldValue())){//配置项存在，并且数据值发生变化
						cbmd.setFieldValue(value);
						Object instance = cbmd.getInstance();
						logger.info("before update, instance:"+printToString(instance));
						Field[] fielsd = instance.getClass().getDeclaredFields();
						for(Field field : fielsd){
							field.setAccessible(true);
							String fieldName = field.getName();
							if(fieldName.equals(cbmd.getFieldName())){
								//常用配置项类型int\long\short\String\boolean
								if(field.getType().equals(int.class) || field.getType().equals(Integer.class)){
									if(value == null || "".equals(value.trim())){
										field.set(instance,0);
									}
									field.set(instance,Integer.parseInt(value));
								}else if(field.getType().equals(long.class) || field.getType().equals(Long.class)){
									if(value == null || "".equals(value.trim())){
										field.set(instance,0l);
									}
									field.set(instance,Long.parseLong(value));
								}else if(field.getType().equals(short.class) || field.getType().equals(Short.class)){
									if(value == null || "".equals(value.trim())){
										field.set(instance,0);
									}
									field.set(instance,Short.parseShort(value));
								}else if(field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)){
									if(value == null || "".equals(value.trim()) || "0".equals(value.trim())){
										field.set(instance,false);
									}else if("1".equals(value.trim())){
										field.set(instance,true);
									}else{
										field.set(instance,Boolean.parseBoolean(value));
									}
									
								}else{
									field.set(instance,value);
								}
								break;
							}
						}
						logger.info("after update, instance:"+ printToString(cbmd.getInstance()));
					}
				}
				
			}
			logger.info("更新内存配置数据成功！");
			return 0;
		} catch (Exception e) {
			logger.error("更新内存数据失败：",e);
		}
		
		return -1;
		
	}
	
	/**
	 * 针对单一属性进行更新内存
	 * @param propValue
	 * @return
	 */
	public static int synchronize(String key_, String propValue){
		try {
			Map<String,ConfBeanMetaData> elements = ElementCollection.getElementCollection();
					String value = propValue;
					ConfBeanMetaData cbmd = elements.get(key_);
					if(cbmd != null && !value.equals(cbmd.getFieldValue())){//配置项存在，并且数据值发生变化
						cbmd.setFieldValue(value);
						Object instance = cbmd.getInstance();
						logger.info("before update, instance:"+printToString(instance));
						Field[] fielsd = instance.getClass().getDeclaredFields();
						for(Field field : fielsd){
							field.setAccessible(true);
							String fieldName = field.getName();
							if(fieldName.equals(cbmd.getFieldName())){
								//常用配置项类型int\long\short\String\boolean
								if(field.getType().equals(int.class) || field.getType().equals(Integer.class)){
									if(value == null || "".equals(value.trim())){
										field.set(instance,0);
									}
									field.set(instance,Integer.parseInt(value));
								}else if(field.getType().equals(long.class) || field.getType().equals(Long.class)){
									if(value == null || "".equals(value.trim())){
										field.set(instance,0l);
									}
									field.set(instance,Long.parseLong(value));
								}else if(field.getType().equals(short.class) || field.getType().equals(Short.class)){
									if(value == null || "".equals(value.trim())){
										field.set(instance,0);
									}
									field.set(instance,Short.parseShort(value));
								}else if(field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)){
									if(value == null || "".equals(value.trim()) || "0".equals(value.trim())){
										field.set(instance,false);
									}else if("1".equals(value.trim())){
										field.set(instance,true);
									}else{
										field.set(instance,Boolean.parseBoolean(value));
									}
									
								}else{
									field.set(instance,value);
								}
								break;
							}
						}
						logger.info("after update, instance:"+ printToString(cbmd.getInstance()));
					}

			logger.info("更新内存配置数据成功！");
			return 0;
		} catch (Exception e) {
			logger.error("更新内存数据失败：",e);
		}
		
		return -1;
		
	}
	/**
	 * 用来返回对象的toString()，获取可读性
	 * @param obj
	 * @return
	 */
	public static String printToString(Object obj){
		try {
			if(obj != null){
				String str = obj.getClass().getSimpleName()+"[";
				Field[] fielsd = obj.getClass().getDeclaredFields();
				for(Field field : fielsd){
					field.setAccessible(true);
					String fieldPair = field.getName()+"="+field.get(obj);
					str += fieldPair+",";
				}
				str += "]";
				return str;
			}else{
				logger.error("input argument is null:"+obj);
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

}
