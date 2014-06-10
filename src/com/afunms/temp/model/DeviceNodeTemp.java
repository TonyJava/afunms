package com.afunms.temp.model;

import com.afunms.common.base.BaseVo;

public class DeviceNodeTemp extends BaseVo {

	private String nodeid;

	private String ip;

	private String type;

	private String subtype;

	private String name;

	private String deviceindex;

	private String dtype;

	private String status;

	private String collecttime;

	public String getCollecttime() {
		return collecttime;
	}

	public String getDeviceindex() {
		return deviceindex;
	}

	public String getDtype() {
		return dtype;
	}

	public String getIp() {
		return ip;
	}

	public String getName() {
		return name;
	}

	public String getNodeid() {
		return nodeid;
	}

	public String getStatus() {
		return status;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getType() {
		return type;
	}

	public void setCollecttime(String string) {
		this.collecttime = string;
	}

	public void setDeviceindex(String deviceindex) {
		this.deviceindex = deviceindex;
	}

	public void setDtype(String dtype) {
		this.dtype = dtype;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setType(String type) {
		this.type = type;
	}

}
