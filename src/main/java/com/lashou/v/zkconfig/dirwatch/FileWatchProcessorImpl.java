package com.lashou.v.zkconfig.dirwatch;

import java.io.File;

import org.apache.log4j.Logger;

import com.lashou.v.zkconfig.common.ActiveKeyValueStore;
import com.lashou.v.zkconfig.common.FileUtils;

public class FileWatchProcessorImpl implements FileWatchProcessor {
	static Logger logger = Logger.getLogger(FileWatchProcessorImpl.class);
	protected ActiveKeyValueStore store;
	@Override
	public Object process(File file) {
		try {
			logger.info("process file :"+file.getAbsolutePath());
			//读取配置文件数据，写入zk服务器。
			byte[] data = FileUtils.readFile(file);
			String path = FileUtils.getFriendlyPath(file.getAbsolutePath());
			this.store.writeByte(FileUtils.getZkPathByConfigPath(path), data);
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
