package com.lashou.v.zkconfig.monitor;

import java.io.File;
import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

import com.lashou.v.zkconfig.common.ActiveKeyValueStore;
import com.lashou.v.zkconfig.common.MemoryDataSynchronizer;
import com.lashou.v.zkconfig.dirwatch.FileWatch;
import com.lashou.v.zkconfig.dirwatch.FileWatchProcessorImpl;

public class ConfFileMonitor extends DataMonitor {
	protected ActiveKeyValueStore store;
	protected String path;
	protected FileWatch fileWatch;


	@Override
	public void updateConfigFile(String zkpath) {
		//这里应该判断文件是否发生了变化，只有发生了变化才执行更新，这样效率高些，也比较安全(防止写入死循环)。
			String filePath = this.getConfigPathByZkPath(zkpath);
			try {
				logger.info("正在更新配置文件数据，filePath=" + filePath);
				byte[] data = store.readByteWithoutWatcher(zkpath);
				this.writeFile(data, filePath);
				File file = new File(filePath);
				fileWatch.setLastModified(file.lastModified());//修改文件监视器的时间戳
			} catch (Exception e) {
				logger.error("新配置文件数据出错，filePath=" + filePath, e);
			}
	}

	@Override
	public void updateMemoryData(String zkpath) {
		try {
			if(zkpath.endsWith(".properties") || zkpath.endsWith(".cfg")){//目前仅实现java系key:value健值对配置文件的内存数据更新
				logger.info("正在更新内存配置项数据，zkpath="+zkpath);
				byte[] data = store.readByteWithoutWatcher(zkpath);
				MemoryDataSynchronizer.synchronize(data);
			}
		}catch (Exception e) {
			logger.error("新内存数据配置出错，zkpath="+zkpath,e);
		}
		
	}
	
	@Override
	public void displayValue() throws KeeperException, InterruptedException {
		String value = store.read(path, this);
		logger.info("Read {" + path + "} as {" + value+ "}");
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {// 当前节点数据发生变化
			logger.info("处理NodeDataChanged事件——>path=" + event.getPath());
			//数据发生变化时候，根据需要更新内存配置数据，以及对应配置文件数据。
			this.updateConfigFile(event.getPath());
			this.updateMemoryData(event.getPath());
			try {
				this.displayValue();////重新注册监听
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else if (event.getType() == EventType.NodeChildrenChanged) {
			logger.info("处理NodeChildrenChanged——path=" + event.getPath());
		}

	}
	@Override
	public void init(ActiveKeyValueStore store, String filePath) throws KeeperException, InterruptedException {
		this.store = store;
		//1 根据文件目录组织zk存储节点目录
		this.path = this.getZkPathByConfigPath(filePath);
		//2 初始化zk存储节点目录
		this.store.initZkPath(path);
		
	}
	@Override
	public void initDataToZk(ActiveKeyValueStore store, String filePath) throws KeeperException, InterruptedException, IOException {
		//4 配置文件初始数据读入zk服务器。
		byte[] data = this.readFile(new File(filePath));
		this.store.writeByte(path, data);
		
	}
	@Override
	public void watchFiles(String filePath) {
		fileWatch = FileWatch.buildWatch();
		FileWatchProcessorImpl processor = new FileWatchProcessorImpl();
		processor.setStore(store);
		fileWatch.runWatch(filePath, processor);
		
	}
	
	@Override
	public void monitor(ActiveKeyValueStore store, String filePath) {
		try {

			//1 根据文件目录组织zk存储节点目录
			//2 初始化zk存储节点目录
			this.init(store, filePath);
			//3 注册事件监听
			this.displayValue();
			//4 配置文件初始数据读入zk服务器。
			this.initDataToZk(store, filePath);
			//5 监听磁盘配置文件变动
			this.watchFiles(filePath);
		} catch (Exception e) {
			logger.error("监听配置文件数据变化出错: in method monitor() of class ConfFileMonitor!", e);
		}

	}




	



}
