package com.lashou.v.zkconfig.monitor;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;

import com.lashou.v.zkconfig.common.ActiveKeyValueStore;
import com.lashou.v.zkconfig.common.MemoryDataSynchronizer;
import com.lashou.v.zkconfig.common.ZkConfigParser;
import com.lashou.v.zkconfig.dirwatch.FileWatch;
import com.lashou.v.zkconfig.dirwatch.PropertiesFileWatchProcessorImpl;

/**
 * Java配置文件专用监视器（支持key:value键值对格式配置的文件扩展名以.propeties | .cfg结尾的文件）
 * @author QuanZhong
 * @version 
 * @since JDK 1.6
 */
public class PropertiesFileMonitor extends ConfFileMonitor {

	@Override
	public void updateConfigFile(String zkpath) {
		
		try {
			logger.info("正在更新配置文件数据，zkpath=" + zkpath);
			String key = zkpath.substring(zkpath.lastIndexOf("/")+1, zkpath.length());
			String value = store.readStringWithoutWatcher(zkpath);
			String fpath = zkpath.substring(0,zkpath.lastIndexOf("/"));//截取掉key值Node
			String filePath = this.getConfigPathByZkPath(fpath);
			ZkConfigParser.updateProperties(filePath, key, value);
			File file = new File(filePath);
			fileWatch.setLastModified(file.lastModified());//修改文件监视器的时间戳
		} catch (Exception e) {
			logger.error("新配置文件数据出错，zkpath=" + zkpath, e);
		}
	}

	@Override
	public void updateMemoryData(String zkpath) {
		try {
				logger.info("正在更新内存配置项数据，zkpath="+zkpath);
				String key = zkpath.substring(zkpath.lastIndexOf("/")+1, zkpath.length());
				String value = store.readStringWithoutWatcher(zkpath);
				MemoryDataSynchronizer.synchronize(key, value);
		}catch (Exception e) {
			logger.error("新内存数据配置出错，zkpath="+zkpath,e);
		}
	}
	@Override
	public void init(ActiveKeyValueStore store, String filePath)
			throws KeeperException, InterruptedException {
		super.init(store, filePath);
		/**
		 * 让每一个key作为zk一个子node
		 */
		if(filePath.endsWith("properties") || filePath.endsWith("cfg")){
			Properties prop = ZkConfigParser.parserFile(filePath);
			if(prop != null){
				Set<Object> set = prop.keySet();
				for(Object key : set){
					String value = prop.getProperty((String) key);
					this.store.updatePathWithOutACL(this.path+"/"+key, value);
				}
					
			}
		}else{
			throw new RuntimeException("PropertiesFileMonitor暂时无法处理此文件类型filePath="+filePath);
		}
	}

	@Override
	public void initDataToZk(ActiveKeyValueStore store, String filePath)
			throws KeeperException, InterruptedException, IOException {
		//super.initDataToZk(store, filePath);
		//这里读取数据过程已经在init中实现，这里覆盖父类实现。
		logger.info("PropertiesFileMonitor初始化数据到ZKServer...");
	}


	@Override
	public void watchFiles(String filePath) {
		fileWatch = FileWatch.buildWatch();
		PropertiesFileWatchProcessorImpl processor = new PropertiesFileWatchProcessorImpl();
		processor.setStore(store);
		fileWatch.runWatch(filePath, processor);
	}
	public void displayPropValue(String zkpath, Watcher watch) throws KeeperException, InterruptedException {
		String value = store.read(zkpath, watch);
		logger.info("Read {" + zkpath + "} as {" + value+ "}");
	}
	public void monitorProps(ActiveKeyValueStore store, String filePath) throws KeeperException, InterruptedException{
		Properties prop = ZkConfigParser.parserFile(filePath);
		Set<Object> set = prop.keySet();
		for(Object key : set){
//			String value = prop.getProperty((String) key);
			String zkPath = this.path+"/"+key;
			PropertiesFileMonitor monitor = new PropertiesFileMonitor();
			monitor.store = store;
			monitor.fileWatch = fileWatch;
			monitor.path = zkPath;
			this.displayPropValue(zkPath, monitor);
		}
		
	}
	
	@Override
	public void monitor(ActiveKeyValueStore store, String filePath) {
		try {
			//1 根据文件目录组织zk存储节点目录
			// 初始化zk存储节点目录
			this.init(store, filePath);
			//2监听磁盘配置文件变动
			this.watchFiles(filePath);
			//3 注册事件监听
			this.monitorProps(store, filePath);
			
		} catch (Exception e) {
			logger.error("监视启动出错：",e);
		}
				
	}

}
