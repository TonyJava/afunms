package com.afunms.system.vo;

import java.io.Serializable;

public class FdbVo implements Serializable {

	private String ifindex;

	private String port;

	private String ipadress;

	private String mac;

	private String collecttime;

	public String getCollecttime() {
		return collecttime;
	}

	public String getIfindex() {
		return ifindex;
	}

	public String getIpadress() {
		return ipadress;
	}

	public String getMac() {
		return mac;
	}

	public String getPort() {
		return port;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	public void setIfindex(String ifindex) {
		this.ifindex = ifindex;
	}

	public void setIpadress(String ipadress) {
		this.ipadress = ipadress;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public void setPort(String port) {
		this.port = port;
	}

}