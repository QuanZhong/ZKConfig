package com.lashou.v.zkconfig;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.KeeperException;

import com.lashou.v.zkconfig.common.ActiveKeyValueStore;

public class ConfigUpdater {
	public static final String PATH = "/config";
	
	private ActiveKeyValueStore store;
	private Random random = new Random(); 
	public ConfigUpdater(String hosts) throws IOException, InterruptedException{
		store = new ActiveKeyValueStore();
		store.connect(hosts);
	}
	
	public void run() throws KeeperException, InterruptedException{
		while(true){
			String value = random.nextInt(100)+"";
			store.write(PATH, value);
			System.out.printf("Set %s to %s\n",PATH,value);
			TimeUnit.SECONDS.sleep(random.nextInt(10));
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		String hosts = "127.0.0.1:2181";
		ConfigUpdater configUpdater = new ConfigUpdater(hosts);
		configUpdater.run();

	}

}
