package com.lashou.v.zkconfig.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;

import com.lashou.v.zkconfig.common.ActiveKeyValueStore;
import com.lashou.v.zkconfig.common.FileUtils;
import com.lashou.v.zkconfig.common.SystemInit;
import com.lashou.v.zkconfig.monitor.Monitor;
import com.lashou.v.zkconfig.monitor.MonitorFactory;
import com.lashou.v.zkconfig.monitor.MonitorFactory.MonitorType;

public class ZkConfigExecutor {
	private static Logger logger = Logger.getLogger(ZkConfigExecutor.class);
	/**
	 * 连接zk服务器，初始化系统参数，返回 ActiveKeyValueStore对象。
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private static ActiveKeyValueStore buildStore() throws KeeperException, InterruptedException, IOException{
		ActiveKeyValueStore store = new ActiveKeyValueStore();
		store.connect();//连接服务器
		SystemInit.init(store);//初始化系统参数
		return store;
	}
	
	/**
	 * 对所有配置目录下所有文件执行监视
	 * @param monitor
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public static void execute(MonitorType type) throws IOException, InterruptedException, KeeperException{
		ActiveKeyValueStore store = buildStore();
		List<File> files = FileUtils.getAllConfigFiles();
		if(files != null && files.size() > 0){
			for(File file : files){
				Monitor monitor = MonitorFactory.getMonitor(type);
				monitor.monitor(store,file.getAbsolutePath());//执行监听
			}
			
		}else{
			logger.error("未获取到任何合法的配置文件，无法执行监控");
			throw new IOException("未获取到任何合法的配置文件，无法执行监控");
		}
	}
	/**
	 * 对所有配置目录下所有文件执行监视
	 * @param monitor
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public static void execute(int type) throws IOException, InterruptedException, KeeperException{
		ActiveKeyValueStore store = buildStore();
		List<File> files = FileUtils.getAllConfigFiles();
		if(files != null && files.size() > 0){
			for(File file : files){
				Monitor monitor = MonitorFactory.getMonitor(MonitorFactory.MonitorType.getMonitorType(type));
				monitor.monitor(store,file.getAbsolutePath());//执行监听
			}
			
		}else{
			logger.error("未获取到任何合法的配置文件，无法执行监控");
			throw new IOException("未获取到任何合法的配置文件，无法执行监控");
		}
	}
	/**
	 * 监视指定配置文件
	 * @param monitor
	 * @param filePath
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public static void executeSpecial(MonitorType type,String filePath) throws IOException, InterruptedException, KeeperException{
		ActiveKeyValueStore store = buildStore();
		Monitor monitor = MonitorFactory.getMonitor(type);
		monitor.monitor(store,filePath);//执行监听
	}
	/**
	 * 监视指定配置文件
	 * @param monitor
	 * @param filePath
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public static void executeSpecial(int type,String filePath) throws IOException, InterruptedException, KeeperException{
		ActiveKeyValueStore store = buildStore();
		Monitor monitor = MonitorFactory.getMonitor(MonitorFactory.MonitorType.getMonitorType(type));
		monitor.monitor(store,filePath);//执行监听
	}

	/**
	 * run local example
	 * @param args
	 */
	public static void main(String[] args) {
		CountDownLatch connectedSignal = new CountDownLatch(1);
		try {
			//1、注册配置所需配置类
			BeanRegisterCenter brc = new BeanRegisterCenter();//执行监听
//			brc.register(new TestObj());
			//2、执行监听
			ZkConfigExecutor.execute(MonitorFactory.MonitorType.CONF_PROPERTIES_FILE_MONITOR);
			
			connectedSignal.await();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		}
		
	}

}
