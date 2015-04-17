package com.lashou.v.zkconfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;

import com.lashou.v.zkconfig.springplugin.ZkConfigScannerConfigurer;

public class TestConfigurer {

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		List<Object> confInstances = new ArrayList<Object>();
		confInstances.add(new TestObj());
		ZkConfigScannerConfigurer zk = new ZkConfigScannerConfigurer();
		zk.setMonitorType(2);
		zk.setConfInstances(confInstances);
		zk.startWatch();


	}

}
