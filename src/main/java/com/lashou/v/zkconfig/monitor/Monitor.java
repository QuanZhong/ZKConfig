package com.lashou.v.zkconfig.monitor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;

import com.lashou.v.zkconfig.common.ActiveKeyValueStore;

public interface Monitor {

	/**
	 * 执行此方法来注册事件监听
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void displayValue() throws KeeperException, InterruptedException;
	/**
	 * 获取所有的系统配置文件,考虑到后期目录监控的便利以及配置的简单性，暂时不支持获取子目录中文件
	 * @return
	 */
	public List<File> getAllConfigFiles();
	/**
	 * 获取所有的系统配置文件路径
	 * @return
	 */
	public List<String> getAllConfigPaths();
	
	/**
	 * 通过配置文件路径，获取zk服务器上的配置路径
	 * @param path
	 * @return
	 */
	public String getZkPathByConfigPath(String path);
	/**
	 * 通过zkpath获取配置文件路径
	 * @param zkpath
	 * @return
	 */
	public String getConfigPathByZkPath(String zkpath);
	/**
	 * 执行monitor业务方法
	 */
	public void monitor(ActiveKeyValueStore store, String filePath);
	/**
	 * 读取磁盘中配置文件内容
	 * @param file
	 * @return
	 */
	public byte[] readFile(File file)throws IOException;
	/**
	 * 想磁盘写入文件内容
	 * @param data
	 * @param filePath
	 * @return
	 */
	public int writeFile(byte[] data,String filePath);
	/**
	 * 初始化，可根据需要具体实现
	 * @param store
	 * @param filePath
	 */
	public void init(ActiveKeyValueStore store, String filePath) throws KeeperException, InterruptedException ;
	/**
	 * 初始化,将配置文件信息读入zk节点，可根据需要具体实现
	 */
	public void initDataToZk(ActiveKeyValueStore store, String filePath) throws KeeperException, InterruptedException, IOException;
	/**
	 * 监听磁盘配置文件变动
	 */
	public void watchFiles(String filePath);
}
