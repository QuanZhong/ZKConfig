package com.lashou.v.zkconfig.common;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * zkconfig.properties配置文件解析辅助类
 * 虚拟机需要配置app.instance.config启动参数，值为存放zkconfig.properties的文件目录
 * @author QuanZhong
 * @version 
 * @since JDK 1.6
 */
public class ZkConfigParser {
	private static String appInstanceConfig;
	private static Logger logger = Logger.getLogger(ZkConfigParser.class);
	private static Properties config = new Properties();
	static {
		String propPath =  System.getProperty("app.instance.config");
		if(propPath == null){
			propPath = appInstanceConfig;
		}
		if(propPath == null){
			logger.error("未设置虚拟机启动参数'app.instance.config'!");
			throw new RuntimeException("未设置虚拟机启动参数'app.instance.config'!");
		}else{
			try {
				InputStream in = new FileInputStream(propPath+File.separator+"zkconfig.properties");
				config.load(in);
				in.close();
			} catch (FileNotFoundException e) {
				logger.error("路劲"+propPath+"下未找到配置文件zkconfig.properties！",e);
			} catch (IOException e) {
				logger.error("读取配置文件zkconfig.properties失败！路劲"+propPath,e);
			}
		}
	 }
	public static String getProperty(String key){
		return config.getProperty(key);
	}
	public static String getAppInstanceConfig() {
		return appInstanceConfig;
	}
	public static void setAppInstanceConfig(String appInstanceConfig) {
		ZkConfigParser.appInstanceConfig = appInstanceConfig;
	}
	public static String getAppConfigPaths(){
		 String value = config.getProperty("app_config_paths");
		 if(value == null || "".equals(value.trim()) || value.indexOf("app.instance.config") > 0){
			 value = System.getProperty("app.instance.config");
		 }
		 if(value == null){
			 logger.warn("zkconfig.properties配置文件缺少参数app_config_paths，或者参数配置不正确！");
		 }
		 return value;
	}
	public static Properties parserFile(String filePath){
		Properties prop = new Properties();
		try {
			InputStream in = new FileInputStream(filePath);
			prop.load(in);
			in.close();
			return prop;
		}catch (Exception e) {
			logger.error("读取配置文件失败！filePath="+filePath,e);
			return null;
		}
	}
	
	public static Properties parserData(byte[] data){
		Properties prop = new Properties();
		try {
			ByteArrayInputStream input = new ByteArrayInputStream(data, 0, data.length);
			InputStream in = new DataInputStream(input);
			prop.load(in);
			in.close();
			return prop;
		}catch (Exception e) {
			logger.error("数据读取失败！",e);
			return null;
		}
	}
    public static synchronized void  updateProperties(String profilepath, String keyname,String keyvalue) {
    	Properties props = new Properties();
        try {
        	InputStream in = new FileInputStream(profilepath);
            props.load(in);
            in.close();
            OutputStream fos = new FileOutputStream(profilepath);           
            props.setProperty(keyname, keyvalue);
            props.store(fos, null);
            fos.close();
            
        } catch (Exception e) {
        	logger.error("属性文件更新错误",e);
        }
    }
	
}
