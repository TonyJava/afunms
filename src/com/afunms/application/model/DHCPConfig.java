package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class DHCPConfig extends BaseVo {

	private int id;
	private String alias;
	private String ipAddress;
	private String community;
	private int mon_flag;
	private String netid;
	private int status;
	private int supperid; 
	private String dhcptype;

	public int getSupperid() {
		return supperid;
	}

	public void setSupperid(int supperid) {
		this.supperid = supperid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public int getMon_flag() {
		return mon_flag;
	}

	public void setMon_flag(int mon_flag) {
		this.mon_flag = mon_flag;
	}

	public String getNetid() {
		return netid;
	}

	public void setNetid(String netid) {
		this.netid = netid;
	}

	public String getDhcptype() {
		return dhcptype;
	}

	public void setDhcptype(String dhcptype) {
		this.dhcptype = dhcptype;
	}

}