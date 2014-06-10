package com.afunms.system.vo;

import java.io.Serializable;

public class ArpVo implements Serializable {

	private String index;

	private String ip;

	private String mac;

	private String collecttime;

	public String getCollecttime() {
		return collecttime;
	}

	public String getIndex() {
		return index;
	}

	public String getIp() {
		return ip;
	}

	public String getMac() {
		return mac;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

}