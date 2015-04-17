package com.lashou.v.zkconfig.monitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.Watcher;

import com.lashou.v.zkconfig.common.ZkCconfigConstant;
import com.lashou.v.zkconfig.common.ZkConfigParser;

public abstract class DataMonitor implements Monitor,Watcher {

	protected static Logger logger = Logger.getLogger(DataMonitor.class);
	/**
	 * 应用系统名称，或者系统代码。用来表示配置文件所属应用，同时也起到数据分组。
	 */
	protected String appName;
	/**
	 * 应用系统配置文件路径，以逗号分隔的字符串
	 */
	protected String appConfigPaths;
	public DataMonitor(){
		//初始化配置参数
		appName = ZkConfigParser.getProperty("app_name");
		appConfigPaths = ZkConfigParser.getAppConfigPaths();
	}
	@Override
	public List<File> getAllConfigFiles() {
		List<File> files = null;
		List<String> confPaths = this.getAllConfigPaths();
		if(confPaths != null && confPaths.size()> 0){
			files = new ArrayList<File>();
			for(String path : confPaths){
				File dirFile = new File(path);
				File[] fs = dirFile.listFiles();
				if(fs != null && fs.length>0){
					for(File f: fs){
						if(f.isFile()){
							files.add(f);
						}
					}
				}
				
			}
		}
		return files;
	}
	@Override
	public List<String> getAllConfigPaths() {
		if(appConfigPaths == null){
			return null;
		}
		String[] paths = appConfigPaths.split(",");
		return Arrays.asList(paths);
	}
	@Override
	public String getZkPathByConfigPath(String path) {
		if(path == null){
			return null;
		}
		String zkpath = ZkCconfigConstant.ROOT_NODE+"/"+appName;
		String friendlyPath = getFriendlyPath(path);
		return zkpath + friendlyPath;
	}
	@Override
	public String getConfigPathByZkPath(String zkpath) {
		if(zkpath == null){
			return null;
		}
		String zkp = ZkCconfigConstant.ROOT_NODE+"/"+appName;
		String path = zkpath.substring(zkp.length(), zkpath.length());
		if(path.indexOf(":") > 0){//包含冒号说明是windows系统，返回路径时候需要去掉首字符
			path = path.substring(1,path.length());
		}
		return path;
	}
	/**
	 * 1、路劲处理，路径分隔符统统转换为/。
	 * 2、如果是windows系统保留盘符部分。eg: /C:/Users/Administrator/Downloads
	 * 3、如果开头没有/则增加
	 * 4、如果结尾有/则删除
	 * @param path
	 * @return
	 */
	protected static String getFriendlyPath(String path){
		if(path != null){
			path = path.replaceAll("\\\\", "/");
//			if(path.indexOf(":") > 0){
//				path = path.substring(path.indexOf(":")+1, path.length());
//			}
			if(!path.startsWith("/")){
				path = "/"+path;
			}
			if(path.endsWith("/")){
				path = path.substring(0,path.length()-1);
				
			}
			return path;
		}else{
			return null;
		}
		
	}

	@Override
	public byte[] readFile(File file) throws IOException {
		if(file != null){
			FileReader reader = new FileReader(file);
			StringBuffer buffer = new StringBuffer();
			char[] cbuf = new char[1024];
			int legth = 0;
			while((legth = reader.read(cbuf)) != -1){
				buffer.append(new String(cbuf,0,legth));
			}
			reader.close();
			return buffer.toString().getBytes(ZkCconfigConstant.CHARSET);
		}
		return null;
	}

	@Override
	public int writeFile(byte[] data, String filePath) {
		try {
			OutputStream out = new FileOutputStream(filePath);
			out.write(data, 0, data.length);
			try {
				out.close();
			} catch (IOException e) {
			}
			return 0;
		} catch (Exception e) {
			logger.error("写入文件出错：filePath="+filePath,e);
		}
		return -1;
	}
	/**
	 * 当数据发生变化时候按需要更新对应配置文件数据
	 * @param zkpath
	 */
	public abstract void updateConfigFile(String zkpath);
	/**
	 * 当数据发生变化时候按需要更新对应内存对象数据
	 * @param zkpath
	 */
	public abstract void updateMemoryData(String zkpath);
}
