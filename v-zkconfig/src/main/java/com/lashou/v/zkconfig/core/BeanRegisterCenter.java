package com.lashou.v.zkconfig.core;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.lashou.v.zkconfig.annotaion.ZkConfig;
import com.lashou.v.zkconfig.annotaion.ZkValue;
import com.lashou.v.zkconfig.bean.ConfBeanMetaData;
import com.lashou.v.zkconfig.common.ElementCollection;
import com.lashou.v.zkconfig.common.RegisterCollection;

/**
 * 需要获取配置参数bean的注册中心
 * @author QuanZhong
 * @version 
 * @since JDK 1.6
 */
public class BeanRegisterCenter {
	private Logger logger = Logger.getLogger(BeanRegisterCenter.class);
	private Map<Object, Class<?>> registers = RegisterCollection.getRegisterCollection();
	private Map<String,ConfBeanMetaData> elements = ElementCollection.getElementCollection();

	
	/**
	 * 注册所需要配置的累对象,支持同一类型，多个实列的配置
	 * @param clazz 类型
	 * @param instance 实例对象
	 * @return
	 */
	public int register(Object instance,Class<?> clazz){
		if (instance != null) {
			registers.put(instance, clazz);
				try {
					this.createElements(instance, clazz);
				} catch (Exception e) {
					logger.error("对象注册失败,请重新注册：",e);
					registers.remove(instance);
					this.deleteElements(clazz);
					return -1;
				}
				return 0;
		}
		return -1;
		
	}
	/**
	 * 注册所需要配置的累对象,支持同一类型，多个实列的配置
	 * @param clazz 类型
	 * @param instance 实例对象
	 * @return
	 */
	public int register(Object instance){
		if (instance != null) {
			Class<?> clazz = instance.getClass();
			registers.put(instance, clazz);
				try {
					this.createElements(instance, clazz);
				} catch (Exception e) {
					logger.error("对象注册失败,请重新注册：",e);
					registers.remove(instance);
					this.deleteElements(clazz);
					return -1;
				}
				return 0;
		}
		return -1;
		
	}
	/**
	 * 删除已注册的配置对象
	 * @param instance
	 * @return
	 */
	public int unregister(Object instance){
		if (instance != null) {
			Object obj = registers.remove(instance);
			if (obj != null) {
				this.deleteElements(instance.getClass());
				return 0;
			}
		}
		return -1;
	}
	/**
	 * 清空所有已注册的配置类对象
	 */
	public void clear(){
		registers.clear();
		elements.clear();
	}
	
	/**
	 * 检测注册中心是否有类配置了ZkConfig注解，如果有配置ZkConfig注解，则将优先以ZkConfg注解中的连接参数，来连接Zookeeper服务器集群
	 */
	public boolean hasZkConfigAnnotationed(){
		boolean result = false;
		Set<Entry<Object, Class<?>>>  entrySet = registers.entrySet();
		for(Entry<Object, Class<?>> entry : entrySet){
			Class<?> clazz = entry.getValue();
			if(clazz != null){
				result = clazz.isAnnotationPresent(ZkConfig.class);
				break;
			}
		}
		return result;
	}
	/**
	 * 获取注册中心ZkConfig配置对象，如果不存在返回null
	 * @return
	 */
	public ZkConfig getZkConfig(){
			Set<Entry<Object, Class<?>>>  entrySet = registers.entrySet();
			for(Entry<Object, Class<?>> entry : entrySet){
				Class<?> clazz = entry.getValue();
				if(clazz != null && clazz.isAnnotationPresent(ZkConfig.class)){
					return clazz.getAnnotation(ZkConfig.class);
				}
			}
		return null;
	}
	/**
	 * 解析出对象中的配置项，并放入ElementCollection容器(新建，如果存在抛异常)
	 * @param instance
	 * @param clazz
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public  void createElements(Object instance,Class<?> clazz) throws IllegalArgumentException, IllegalAccessException{
		if(instance != null){
			//读取属性配置
			Field[] fielsd = clazz.getDeclaredFields();
			for(Field field : fielsd){
				field.setAccessible(true);
				if(field.isAnnotationPresent(ZkValue.class)){
					ZkValue zkValue = field.getAnnotation(ZkValue.class);
					String key = zkValue.key();
					ConfBeanMetaData md = elements.get(key);
					if(md == null){
						String fieldName = field.getName();
						Object fieldValue = field.get(instance);
						Class<?> fieldType = field.getType();
						md = new ConfBeanMetaData(instance, key, fieldName, fieldValue, fieldType);
						elements.put(key, md);
						
					}else{
						String msg = "系统中配置属性key重复，已有相同名称的key值配置。请修改@ZkValue中key属性值。key="+key;
						logger.warn(msg);
						throw new IllegalArgumentException(msg);
					}
					
					
				}
			}
		}
	}
	/**
	 * 解析出对象中的配置项，并放入ElementCollection容器(更新，如果不存在则新建)
	 * @param instance
	 * @param clazz
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public  void updateElements(Object instance,Class<?> clazz) throws IllegalArgumentException, IllegalAccessException{
		if(instance != null){
			//读取属性配置
			Field[] fielsd = clazz.getDeclaredFields();
			for(Field field : fielsd){
				field.setAccessible(true);
				if(field.isAnnotationPresent(ZkValue.class)){
					ZkValue zkValue = field.getAnnotation(ZkValue.class);
					String key = zkValue.key();
					ConfBeanMetaData md = elements.get(key);
					if(md == null){
						String fieldName = field.getName();
						Object fieldValue = field.get(instance);
						Class<?> fieldType = field.getType();
						md = new ConfBeanMetaData(instance, key, fieldName, fieldValue, fieldType);
						elements.put(key, md);
						
					}else{
						String fieldName = field.getName();
						Object fieldValue = field.get(instance);
						Class<?> fieldType = field.getType();
						md.setFieldName(fieldName);
						md.setFieldValue(fieldValue);
						md.setFieldType(fieldType);
						md.setInstance(instance);
						md.setKey(key);
						elements.put(key, md);
					}
					
					
				}
			}
		}
	}
	public  void deleteElements(Class<?> clazz){

			Field[] fielsd = clazz.getDeclaredFields();
			for(Field field : fielsd){
				field.setAccessible(true);
				if(field.isAnnotationPresent(ZkValue.class)){
					ZkValue zkValue = field.getAnnotation(ZkValue.class);
					String key = zkValue.key();
					elements.remove(key);
				}
			}
		}
}
