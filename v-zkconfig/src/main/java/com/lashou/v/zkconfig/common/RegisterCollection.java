package com.lashou.v.zkconfig.common;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存配置类注册信息
 */
public class RegisterCollection extends ConcurrentHashMap<Object, Class<?>>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2708705056582718018L;
	
	private static RegisterCollection  instanceCollection = null;

	private RegisterCollection(){
	}
	/**
	 * 获取已注册的配置类集合对象key:对象实例；value class。
	 * @return
	 */
	public static RegisterCollection getRegisterCollection(){
		if(instanceCollection == null){
			synchronized (RegisterCollection.class) {
				instanceCollection = new RegisterCollection();
			}
		}
		return instanceCollection;
	}
}
