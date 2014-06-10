package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class AclBase extends BaseVo {
	private int id;
	private String ipaddress;
	private String name;

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getName() {
		return name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddres(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setName(String name) {
		this.name = name;
	}

}
