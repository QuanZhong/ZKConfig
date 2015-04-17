package com.lashou.v.zkconfig.dirwatch;

import java.io.File;

/**
 * 目录监视处理器，当目录监视器发现目录中文件该变时候会回调process方法返回改变后的文件列表
 * @author quanzhong
 *
 */
public interface FileWatchProcessor {

	public Object process(File file);
}
