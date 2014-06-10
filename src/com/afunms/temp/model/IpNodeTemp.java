package com.afunms.temp.model;

import com.afunms.common.base.BaseVo;

public class IpNodeTemp extends BaseVo {

	private int id;

	private String aliasip;

	private String indexs;

	private String ipaddress;

	private String descr;

	private String speeds;

	private String types;

	public String getAliasip() {
		return aliasip;
	}

	public String getDescr() {
		return descr;
	}

	public int getId() {
		return id;
	}

	public String getIndexs() {
		return indexs;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getSpeeds() {
		return speeds;
	}

	public String getTypes() {
		return types;
	}

	public void setAliasip(String aliasip) {
		this.aliasip = aliasip;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIndexs(String indexs) {
		this.indexs = indexs;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setSpeeds(String speeds) {
		this.speeds = speeds;
	}

	public void setTypes(String types) {
		this.types = types;
	}

}
