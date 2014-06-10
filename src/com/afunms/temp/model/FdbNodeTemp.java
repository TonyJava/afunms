package com.afunms.temp.model;

import com.afunms.common.base.BaseVo;

public class FdbNodeTemp extends BaseVo {

	private String nodeid;

	private String ip;

	private String type;

	private String subtype;

	private String ifindex;

	private String ipaddress;

	private String mac;

	private String ifband;

	private String ifsms;

	private String collecttime;

	private String bak;

	public String getBak() {
		return bak;
	}

	public String getCollecttime() {
		return collecttime;
	}

	public String getIfband() {
		return ifband;
	}

	public String getIfindex() {
		return ifindex;
	}

	public String getIfsms() {
		return ifsms;
	}

	public String getIp() {
		return ip;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getMac() {
		return mac;
	}

	public String getNodeid() {
		return nodeid;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getType() {
		return type;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public void setCollecttime(String string) {
		this.collecttime = string;
	}

	public void setIfband(String ifband) {
		this.ifband = ifband;
	}

	public void setIfindex(String ifindex) {
		this.ifindex = ifindex;
	}

	public void setIfsms(String ifsms) {
		this.ifsms = ifsms;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setType(String type) {
		this.type = type;
	}

}
