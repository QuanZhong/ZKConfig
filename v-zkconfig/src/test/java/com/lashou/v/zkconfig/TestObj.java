package com.lashou.v.zkconfig;

import com.lashou.v.zkconfig.annotaion.ZkValue;

//@ZkConfig(hosts = "127.0.0.1:2181")
public class TestObj {

	@ZkValue(key = "ip")
	private String ip;
	@ZkValue(key = "port")
	private int port;
	private String sth;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSth() {
		return sth;
	}

	public void setSth(String sth) {
		this.sth = sth;
	}

	@Override
	public String toString() {
		return "TestObj [ip=" + ip + ", port=" + port + ", sth=" + sth + "]";
	}

}
