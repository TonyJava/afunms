package com.afunms.ip.stationtype.model;

import com.afunms.common.base.BaseVo;

public class encryptiontype extends BaseVo {

	private int id;
	private String name;
	private String descr;
	private String bak;

	public String getBak() {
		return bak;
	}

	public String getDescr() {
		return descr;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

}
