package com.lashou.v.zkconfig.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class FileUtils {
	static Logger logger = Logger.getLogger(FileUtils.class);
	/**
	 * 应用系统名称，或者系统代码。用来表示配置文件所属应用，同时也起到数据分组。
	 */
	private static  String appName;
	/**
	 * 应用系统配置文件路径，以逗号分隔的字符串
	 */
	private static String appConfigPaths;
	
	static {
		appName = ZkConfigParser.getProperty("app_name");
		appConfigPaths = ZkConfigParser.getAppConfigPaths();
	}
	

	public static byte[] readFile(File file) throws IOException {
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


	public static int writeFile(byte[] data, String filePath) {
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
	
	public static String getConfigPathByZkPath(String zkpath) {
		if(zkpath == null){
			return null;
		}
		String zkp = ZkCconfigConstant.ROOT_NODE+"/"+ZkConfigParser.getProperty("app_name");
		String path = zkpath.substring(zkp.length(), zkpath.length());
		if(path.indexOf(":") > 0){//包含冒号说明是windows系统，返回路径时候需要去掉首字符
			path = path.substring(1,path.length());
		}
		return path;
	}
	
	public static String getZkPathByConfigPath(String path) {
		if(path == null){
			return null;
		}
		String zkpath = ZkCconfigConstant.ROOT_NODE+"/"+ZkConfigParser.getProperty("app_name");;
		String friendlyPath = getFriendlyPath(path);
		return zkpath + friendlyPath;
	}
	
	/**
	 * 1、路劲处理，路径分隔符统统转换为/。
	 * 2、如果是windows系统保留盘符部分。eg: /C:/Users/Administrator/Downloads
	 * 3、如果开头没有/则增加
	 * 4、如果结尾有/则删除
	 * @param path
	 * @return
	 */
	public static String getFriendlyPath(String path){
		if(path != null){
			path = path.replaceAll("\\\\", "/");
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

	public static List<File> getAllConfigFiles() {
		List<File> files = null;
		List<String> confPaths = getAllConfigPaths();
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

	public static List<String> getAllConfigPaths() {
		if(appConfigPaths == null){
			return null;
		}
		String[] paths = appConfigPaths.split(",");
		return Arrays.asList(paths);
	}

	public static String getAppName() {
		return appName;
	}


	public static void setAppName(String appName) {
		FileUtils.appName = appName;
	}


	public static String getAppConfigPaths() {
		return appConfigPaths;
	}


	public static void setAppConfigPaths(String appConfigPaths) {
		FileUtils.appConfigPaths = appConfigPaths;
	}
	
}
