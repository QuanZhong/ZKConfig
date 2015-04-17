package com.lashou.v.zkconfig.springplugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.lashou.v.zkconfig.core.BeanRegisterCenter;
import com.lashou.v.zkconfig.core.ZkConfigExecutor;
import com.lashou.v.zkconfig.monitor.MonitorFactory;

public class ZkConfigScannerConfigurer implements InitializingBean,ApplicationContextAware {
	private Logger logger = Logger.getLogger(ZkConfigScannerConfigurer.class);
	
	/**
	 * 想要执行的监控类型
	 * @see MonitorFactory.MonitorType
	 */
	private int monitorType;
	/**
	 * 指定执行的特定文件
	 */
	private String specialFilePath;
	/**
	 * 配置文件对应实例对象 @ZkConfig注解的对象
	 */
	private List<Object> confInstances;
	
	/**
	 * 配置文件对应实例对象名称，spring中的bean id
	 */
	private List<String> confInstanceNames;
	
	ApplicationContext applicationContext;
	
	public void startWatch() throws IOException, InterruptedException, KeeperException{
		//注册配置所需配置类
		BeanRegisterCenter brc = new BeanRegisterCenter();//执行监听
		if(confInstances != null && confInstances.size() > 0){
			for(Object instance : confInstances){
				brc.register(instance);
			}
		}else{
			logger.warn("无任何配置类对象被注册！");
		}
		if(monitorType > 0){
			ZkConfigExecutor.execute(monitorType);
		}else{
			logger.error("monitorType类型未指定，无法启动数据监听！ ");
			throw new RuntimeException("monitorType类型未指定，无法启动数据监听！ ");
		}
		
	}
	public void startSpecialWatch(String filePath) throws IOException, InterruptedException, KeeperException{
		//注册配置所需配置类
		BeanRegisterCenter brc = new BeanRegisterCenter();//执行监听
		if(confInstances != null && confInstances.size() > 0){
			for(Object instance : confInstances){
				brc.register(instance);
			}
		}else{
			logger.warn("无任何配置类对象被注册！");
		}
		if(monitorType > 0){
			ZkConfigExecutor.executeSpecial(monitorType, filePath);
		}else{
			logger.error("monitorType类型未指定，无法启动数据监听！ ");
			throw new RuntimeException("monitorType类型未指定，无法启动数据监听！ ");
		}
		
	}

	public int getMonitorType() {
		return monitorType;
	}

	public void setMonitorType(int monitorType) {
		this.monitorType = monitorType;
	}

	public List<Object> getConfInstances() {
		return confInstances;
	}

	public void setConfInstances(List<Object> confInstances) {
		this.confInstances = confInstances;
	}
	
	public List<String> getConfInstanceNames() {
		return confInstanceNames;
	}

	public void setConfInstanceNames(List<String> confInstanceNames) {
		this.confInstanceNames = confInstanceNames;
	}
	
	public String getSpecialFilePath() {
		return specialFilePath;
	}
	public void setSpecialFilePath(String specialFilePath) {
		this.specialFilePath = specialFilePath;
	}

	//如果实现ApplicationContextAware,调用setApplicationContext设置ApplicationContext
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
			
	}
	public void initParameters(){
		if((confInstanceNames !=null && confInstanceNames.size() > 0) && (confInstances == null || confInstances.size() ==0) && applicationContext != null){
			confInstances = new ArrayList<Object>();
			for(String beanName : confInstanceNames){
				Object confObj = applicationContext.getBean(beanName);
				confInstances.add(confObj);
			}
			
		}
	}
	//调用InitializingBean的afterPropertiesSet()方法;
	@Override
	public void afterPropertiesSet() throws Exception {
		initParameters();
		new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					int counter = 0;
					while(true){//延迟十秒等待web容器类加载，防止由于类加载不成功导致系统无法运行。
						Thread.sleep(1000);
						counter+=10;
						logger.info("ZKConfig Server即将启动运行，正在初始化..."+counter+"%");
						if(counter >=100){
							break;
						}
					}
					if(specialFilePath == null || "".equals(specialFilePath.trim())){
						startWatch();
					}else{
						startSpecialWatch(specialFilePath);
					}
					
				}catch (Exception e) {
					logger.error("系统启动监听出错：",e);
				}
				
			}
			
		}).start();
		
	}

	

	
	
	

}
