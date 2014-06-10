package com.afunms.polling.om;

import java.io.Serializable;

/**
 * cmts中的地址属性
 * 
 * @author Administrator
 * 
 */
public class CMTSaddresscollectdata implements Serializable {

	private String ipAddress;// IP地址

	private String macAddress;// MAC地址

	private String statusAddress;// 地址状态

	private String collecttime;// 采集时间

	public String getCollecttime() {
		return collecttime;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public String getStatusAddress() {
		return statusAddress;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public void setStatusAddress(String statusAddress) {
		this.statusAddress = statusAddress;
	}
}
