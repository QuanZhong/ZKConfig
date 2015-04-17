package com.lashou.v.zkconfig.dirwatch;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

import com.lashou.v.zkconfig.common.ZkConfigParser;

/**
 * 用于监视文件变动
 * 
 * @author quanzhong
 * 
 */
public class FileWatch {
	static Logger logger = Logger.getLogger(FileWatch.class);
	private  long lm = 0;
	private  boolean isRunning = false;
	private  String cmd = "";
	private boolean isUpdateFromZk = false;
	/**
	 * 获取fileWatch实例
	 * 
	 * @return
	 */
	public static FileWatch buildWatch() {
		return new FileWatch();
	}

	public synchronized long getLastModified(){
		return lm;
	}
	public synchronized void setLastModified(long lm){
		this.lm = lm;
	}
	
	public synchronized boolean isUpdateFromZk() {
		return isUpdateFromZk;
	}

	public synchronized void setUpdateFromZk(boolean isUpdateFromZk) {
		this.isUpdateFromZk = isUpdateFromZk;
	}

	/**
	 * 启动目录监视(建议：最好在单独线程里执行此方法，因：此方法会阻塞主线程，导致后续程序无法执行)
	 * 
	 * @param dirPath
	 *            目录路劲
	 * @param period
	 *            单位毫秒，表示监控时间间隔
	 * @throws Exception
	 */
	public void startWatch(String filePath, long period, FileWatchProcessor processor) throws Exception {
		if (filePath == null) {
			throw new NullPointerException("文件路径不可以为空");
		}
		if(period <= 0){
			throw new Exception("参数period必须大于0");
		}
		if(processor == null){
			throw new NullPointerException("fileWatchProcessor 对象不能为空。");
		}
		File file = new File(filePath);
		if(!file.exists()){
			throw new FileNotFoundException("文件路径不存在："+filePath);
		}
		if (!isRunning) {
			this.setLastModified(file.lastModified());
			boolean fileExists;
			while (fileExists = file.exists() && !"quite".equals(cmd)) {
				logger.info("正在对文件进行监视...filePath="+filePath);
				isRunning = true;
				long nlm = file.lastModified();
				if (nlm > this.getLastModified()) {
					logger.info("文件内容发生变化filePath=" + filePath);
					processor.process(new File(filePath));
					this.setLastModified(nlm);
					this.setUpdateFromZk(false);
				}
				Thread.sleep(period);
			}
			if(!fileExists && isRunning){
				isRunning = false;
				throw new FileNotFoundException("文件"+filePath+"无法访问。");
			}
		}
	}
	/**
	 * /**
	 * 启动目录监视
	 * 
	 * @param dirPath
	 *            目录路劲
	 * @param period
	 *            单位毫秒，表示监控时间间隔
	 * @throws Exception
	 */
	public void runWatch(final String filePath, final long period, final FileWatchProcessor processor){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					startWatch(filePath, period, processor);
				} catch (Exception e) {
					logger.error("启动目录监视出错：filePath="+filePath);
				}
			}
			
		}).start();
	}
	
	/**
	 * /**
	 * 启动目录监视
	 * 
	 * @param dirPath
	 *            目录路劲
	 * @param period
	 *            单位毫秒，表示监控时间间隔
	 * @throws Exception
	 */
	public void runWatch(final String filePath, final FileWatchProcessor processor){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					int period = 1000*15;//默认十五秒
					String dwp = ZkConfigParser.getProperty("dir_watch_period");
					if(dwp !=null && !"".equals(dwp.trim())){
						period = Integer.parseInt(dwp);
					}
					startWatch(filePath, period, processor);
				} catch (Exception e) {
					logger.error("启动目录监视出错：filePath="+filePath);
				}
			}
			
		}).start();
	}
	/**
	 * 停止目录监视
	 */
	public  void stopWatch() {
		isRunning = false;
		cmd = "quite";
	}

}
