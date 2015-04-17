package com.lashou.v.zkconfig.common;

import java.util.concurrent.ConcurrentHashMap;

import com.lashou.v.zkconfig.bean.ConfBeanMetaData;

/**
 * 获取已注册的配置属性集合key：属性名称；value：对象实例（instanceCollection中key的值）
 */
public class ElementCollection extends ConcurrentHashMap<String,ConfBeanMetaData>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2708705056582718018L;
	
	private static ElementCollection  elementCollection = null;

	private ElementCollection(){
	}
	/**
	 * 获取已注册的配置属性集合,key：ZkConfig中标记的名称；value：对象实例（RegisterCollection中key的值）
	 * @return
	 */
	public static ElementCollection getElementCollection(){
		if(elementCollection == null){
			synchronized (ElementCollection.class) {
				elementCollection = new ElementCollection();
			}
		}
		return elementCollection;
	}
	
}
