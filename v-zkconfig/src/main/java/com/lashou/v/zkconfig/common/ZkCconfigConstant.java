package com.lashou.v.zkconfig.common;

import java.nio.charset.Charset;

/**
 * 系统常量
 * ClassName: ZkCconfigConstant <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date: 2015年4月14日 下午4:38:48 <br/>
 *
 * @author QuanZhong
 * @version 
 * @since JDK 1.6
 */
public class ZkCconfigConstant {

	/**
	 * 系统配置zk服务上的根节点
	 */
	public static final String ROOT_NODE = "/zkconfig";
	/**
	 * zk节点数据默认值
	 */
	public static final String DEFAULT_NODE_DATA = "-1";
	/**
	 * 系统默认字符编码
	 */
	public static final Charset CHARSET = Charset.forName("UTF-8");
}
