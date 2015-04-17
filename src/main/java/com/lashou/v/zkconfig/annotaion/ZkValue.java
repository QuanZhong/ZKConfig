package com.lashou.v.zkconfig.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于解析配置文件的注解
 * @author QuanZhong
 * @version 
 * @since JDK 1.6
 */
@Target(value={ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ZkValue {
	/**
	 * 配置文件key
	 * @return
	 */
	String key();
}
