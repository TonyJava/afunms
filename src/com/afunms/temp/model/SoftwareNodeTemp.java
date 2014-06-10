package com.afunms.temp.model;

import com.afunms.common.base.BaseVo;

public class SoftwareNodeTemp extends BaseVo {

	private String nodeid;

	private String ip;

	private String type;

	private String subtype;

	private String name;

	private String swid;

	private String stype;

	private String insdate;

	private String collecttime;

	public String getCollecttime() {
		return collecttime;
	}

	public String getInsdate() {
		return insdate;
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

	public String getStype() {
		return stype;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getSwid() {
		return swid;
	}

	public String getType() {
		return type;
	}

	public void setCollecttime(String string) {
		this.collecttime = string;
	}

	public void setInsdate(String insdate) {
		this.insdate = insdate;
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

	public void setStype(String stype) {
		this.stype = stype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setSwid(String swid) {
		this.swid = swid;
	}

	public void setType(String type) {
		this.type = type;
	}

}
