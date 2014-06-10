package com.afunms.temp.model;

import com.afunms.common.base.BaseVo;

public class StorageNodeTemp extends BaseVo {

	private String nodeid;

	private String ip;

	private String type;

	private String subtype;

	private String name;

	private String storageindex;

	private String stype;

	private String cap;

	private String collecttime;

	public String getCap() {
		return cap;
	}

	public String getCollecttime() {
		return collecttime;
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

	public String getStorageindex() {
		return storageindex;
	}

	public String getStype() {
		return stype;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getType() {
		return type;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public void setCollecttime(String string) {
		this.collecttime = string;
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

	public void setStorageindex(String storageindex) {
		this.storageindex = storageindex;
	}

	public void setStype(String stype) {
		this.stype = stype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setType(String type) {
		this.type = type;
	}
}
