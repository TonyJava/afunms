package com.afunms.polling.om;

import java.io.Serializable;

/**
 * cmts�еĵ�ַ����
 * 
 * @author Administrator
 * 
 */
public class CMTSaddresscollectdata implements Serializable {

	private String ipAddress;// IP��ַ

	private String macAddress;// MAC��ַ

	private String statusAddress;// ��ַ״̬

	private String collecttime;// �ɼ�ʱ��

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
