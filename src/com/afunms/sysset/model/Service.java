package com.afunms.sysset.model;

import com.afunms.common.base.BaseVo;

public class Service extends BaseVo {
	private int id;
	private String service;
	private int port;
	private int scan;
	private int timeOut;

	public int getId() {
		return id;
	}

	public int getPort() {
		return port;
	}

	public String getService() {
		return service;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public int isScan() {
		return scan;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setScan(int scan) {
		this.scan = scan;
	}

	public void setService(String service) {
		this.service = service;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
}
