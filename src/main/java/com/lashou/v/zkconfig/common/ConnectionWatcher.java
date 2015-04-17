package com.lashou.v.zkconfig.common;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.lashou.v.zkconfig.annotaion.ZkConfig;
import com.lashou.v.zkconfig.core.BeanRegisterCenter;

/**
 * 用于等待建立zookeeper连接的辅助类
   @author QuanZhong
 * @version 
 * @since JDK 1.6
 */
public class ConnectionWatcher implements Watcher {
	protected Logger logger = Logger.getLogger(ConnectionWatcher.class);
	/**
	 * 会话超时时间
	 */
	private static  int sessionTimeout = 5000;
	private static String hosts;
	protected ZooKeeper zk;
	private CountDownLatch connectedSignal = new CountDownLatch(1);
	
	public ConnectionWatcher(){
		//优先读取注解配置信息
		BeanRegisterCenter registerCenter = new BeanRegisterCenter();
		if(registerCenter.hasZkConfigAnnotationed()){
			ZkConfig zkConfig = registerCenter.getZkConfig();
			if(zkConfig != null){
				hosts = zkConfig.hosts();
				sessionTimeout = zkConfig.sessionTimeout();
			}
		}
		//其次读取配置文件配置信息
		if(hosts == null){
			hosts = ZkConfigParser.getProperty("hosts");
			String sto = ZkConfigParser.getProperty("sessionTimeout");
			if(sto != null && !"".equals(sto.trim())){
				try {
					sessionTimeout = Integer.parseInt(sto);
				} catch (NumberFormatException e) {
					logger.error("zkconfig.properties中配置参数sessionTimeout有误!",e);
				}
			}
		}
		
	}
	
	/**
	 * zk连接字符串 ip:poort(指定主机的方式连接，此方式不会修改默认配置方式，单独对此次连接管用)
	 * @param hosts
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void connect(String hosts) throws IOException, InterruptedException{
		zk = new ZooKeeper(hosts,sessionTimeout,this);
		connectedSignal.await();//利用锁存器来等待连接成功，（只有连接成功后续zk对象才能正常使用）
		
	}
	/**
	 * zk连接 需要提前配置hosts，如果hosts为空将异常
	 * @param hosts
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void connect() throws IOException, InterruptedException{
		zk = new ZooKeeper(hosts,sessionTimeout,this);
		connectedSignal.await();//利用锁存器来等待连接成功，（只有连接成功后续zk对象才能正常使用）
		
	}

	@Override
	public void process(WatchedEvent event) {
		logger.info("method process be called in class ConnectionWatcher.");
		if(event.getState() == Watcher.Event.KeeperState.SyncConnected){
			logger.info("The client connect to the zookeeper server successfully!");
			connectedSignal.countDown();
		}
	}
	
	public void close() throws InterruptedException{
		zk.close();
	}

	public static String getHosts() {
		return hosts;
	}

	public static void setHosts(String hosts) {
		ConnectionWatcher.hosts = hosts;
	}

	public static int getSessionTimeout() {
		return sessionTimeout;
	}

	public static void setSessionTimeout(int sessionTimeout) {
		ConnectionWatcher.sessionTimeout = sessionTimeout;
	}

}
