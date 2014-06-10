package com.afunms.config.model;

import java.sql.Timestamp;

public class ConfiguringDevice {
	private int id;
	private String ipaddress;
	private String alias;
	private int category;// �豸���ͣ������� ·���� ����
	private Timestamp lastUpdateTime;
	private String prompt;
	private int enablevpn;
	private int isSynchronized;// 1ͬ�� 0��ͬ��
	private String deviceRender;// �����豸���̣�h3c cisco

	public String getAlias() {
		return alias;
	}

	public int getCategory() {
		return category;
	}

	public String getDeviceRender() {
		return deviceRender;
	}

	public int getEnablevpn() {
		return enablevpn;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public int getIsSynchronized() {
		return isSynchronized;
	}

	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public void setDeviceRender(String deviceRender) {
		this.deviceRender = deviceRender;
	}

	public void setEnablevpn(int enablevpn) {
		this.enablevpn = enablevpn;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setIsSynchronized(int isSynchronized) {
		this.isSynchronized = isSynchronized;
	}

	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
}
