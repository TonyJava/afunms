package com.afunms.polling.om;

import java.io.Serializable;

public class HillstoneSysCurSessionCollectData implements Serializable {
	private Long id;
	private String ipaddress;
	private String category;
	private String entity;
	private String subentity;
	private String thevalue;
	private java.util.Calendar collecttime;
	private String unit;

	public String getCategory() {
		return category;
	}

	public java.util.Calendar getCollecttime() {
		return collecttime;
	}

	public String getEntity() {
		return entity;
	}

	public Long getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getSubentity() {
		return subentity;
	}

	public String getThevalue() {
		return thevalue;
	}

	public String getUnit() {
		return unit;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setCollecttime(java.util.Calendar collecttime) {
		this.collecttime = collecttime;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setSubentity(String subentity) {
		this.subentity = subentity;
	}

	public void setThevalue(String thevalue) {
		this.thevalue = thevalue;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
