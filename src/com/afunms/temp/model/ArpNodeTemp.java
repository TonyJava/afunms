package com.afunms.temp.model;

import com.afunms.common.base.BaseVo;

public class ArpNodeTemp extends BaseVo {

	private int id;

	private String relateipaddr;

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

	public int getId() {
		return id;
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

	public String getIpaddress() {
		return ipaddress;
	}

	public String getMac() {
		return mac;
	}

	public String getRelateipaddr() {
		return relateipaddr;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public void setCollecttime(String string) {
		this.collecttime = string;
	}

	public void setId(int id) {
		this.id = id;
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

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public void setRelateipaddr(String relateipaddr) {
		this.relateipaddr = relateipaddr;
	}

}
