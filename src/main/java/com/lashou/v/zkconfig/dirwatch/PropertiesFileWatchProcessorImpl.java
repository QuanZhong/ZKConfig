package com.lashou.v.zkconfig.dirwatch;

import java.io.File;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.lashou.v.zkconfig.common.ActiveKeyValueStore;
import com.lashou.v.zkconfig.common.FileUtils;
import com.lashou.v.zkconfig.common.ZkConfigParser;

public class PropertiesFileWatchProcessorImpl implements FileWatchProcessor {
	static Logger logger = Logger.getLogger(PropertiesFileWatchProcessorImpl.class);
	protected ActiveKeyValueStore store;
	@Override
	public Object process(File file) {
		try {
			logger.info("process file :"+file.getAbsolutePath());
			String zkPath = FileUtils.getZkPathByConfigPath(FileUtils.getFriendlyPath(file.getAbsolutePath()));
			//读取配置文件数据，写入zk服务器。
			Properties prop = ZkConfigParser.parserFile(file.getAbsolutePath());
			if(prop != null){
				Set<Object> set = prop.keySet();
				for(Object key : set){
					String value = prop.getProperty((String) key);
					String zkValue = this.store.readStringWithoutWatcher(zkPath+"/"+key);
					if(!value.equals(zkValue)){//发生变更，则更新
						this.store.updatePathWithOutACL(zkPath+"/"+key, value);
					}
				}
					
			}
		} catch (Exception e) {
			logger.error("更新变更到zk出错。",e);
		}
		return file;
	}
	public ActiveKeyValueStore getStore() {
		return store;
	}
	public void setStore(ActiveKeyValueStore store) {
		this.store = store;
	}

}
