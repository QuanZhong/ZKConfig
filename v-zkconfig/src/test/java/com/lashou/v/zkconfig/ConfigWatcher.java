package com.lashou.v.zkconfig;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

import com.lashou.v.zkconfig.common.ActiveKeyValueStore;

public class ConfigWatcher implements Watcher {

	private ActiveKeyValueStore store;
	
	public ConfigWatcher() throws IOException, InterruptedException, KeeperException{
		store = new ActiveKeyValueStore();
		store.connect();
		
	}

	public ConfigWatcher(String hosts) throws IOException, InterruptedException, KeeperException{
		store = new ActiveKeyValueStore();
		store.connect(hosts);
		
	}
	public void displayConfig() throws KeeperException, InterruptedException{
		String value = store.read(ConfigUpdater.PATH, this);
		System.out.printf("Read %s as %s\n", ConfigUpdater.PATH,value);
		List<String>  children = store.getChildren(ConfigUpdater.PATH, 
			new Watcher(){

			@Override
			public void process(WatchedEvent event) {
				if(event.getType() == Watcher.Event.EventType.NodeDataChanged){//当前节点数据发生变化
					System.out.println("子节点发生了变化1,"+event.getPath());
				}else if(event.getType() == EventType.NodeChildrenChanged){
					System.out.println("子节点发生了变化2,"+event.getPath());
					
				}
				
			}
			
		}
		);
		System.out.printf("Read %s children nodes as %s\n", ConfigUpdater.PATH,children);
	}
	@Override
	public void process(WatchedEvent event) {
		if(event.getType() == Watcher.Event.EventType.NodeDataChanged){//当前节点数据发生变化
			try {
				this.displayConfig();
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else if(event.getType() == EventType.NodeChildrenChanged){
			System.out.println("子节点发生了变化0,"+event.getPath());
		}

	}

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		CountDownLatch connectedSignal = new CountDownLatch(1);
		
//		String hosts = "127.0.0.1:2181";
		ConfigWatcher configWatcher = new ConfigWatcher();
		configWatcher.displayConfig();
		connectedSignal.await();

	}

}
