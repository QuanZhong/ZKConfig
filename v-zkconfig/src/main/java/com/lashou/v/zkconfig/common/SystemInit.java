package com.lashou.v.zkconfig.common;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

/**
 * 
	执行部分系统参数的初始化工作
 * @author QuanZhong
 * @version 
 * @since JDK 1.6
 */
public class SystemInit {

	/**
	 * 系统数据初始化工作
	 * @param store
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public static void init(ActiveKeyValueStore store) throws KeeperException, InterruptedException{
		initRootNode(store);
		
	}
	/**
	 * 初始化系统根节点
	 * @param store
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public static void initRootNode(ActiveKeyValueStore store) throws KeeperException, InterruptedException{
		Stat stat = store.zk.exists(ZkCconfigConstant.ROOT_NODE, false);
		if(stat == null){
			store.zk.create(ZkCconfigConstant.ROOT_NODE, ZkCconfigConstant.DEFAULT_NODE_DATA.getBytes(ZkCconfigConstant.CHARSET), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
	}
}
