package com.afunms.temp.model;

import com.afunms.common.base.BaseVo;

public class NodeTemp extends BaseVo {

	private String nodeid;

	private String ip;

	private String type;

	private String subtype;

	private String entity;

	private String subentity;

	private String thevalue;

	private String chname;

	private String restype;

	private String unit;

	private String sindex;

	private String collecttime;

	private String bak;

	public String getBak() {
		return bak;
	}

	public String getChname() {
		return chname;
	}

	public String getCollecttime() {
		return collecttime;
	}

	public String getEntity() {
		return entity;
	}

	public String getIp() {
		return ip;
	}

	public String getNodeid() {
		return nodeid;
	}

	public String getRestype() {
		return restype;
	}

	public String getSindex() {
		return sindex;
	}

	public String getSubentity() {
		return subentity;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getThevalue() {
		return thevalue;
	}

	public String getType() {
		return type;
	}

	public String getUnit() {
		return unit;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public void setChname(String chname) {
		this.chname = chname;
	}

	public void setCollecttime(String string) {
		this.collecttime = string;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public void setRestype(String restype) {
		this.restype = restype;
	}

	public void setSindex(String sindex) {
		this.sindex = sindex;
	}

	public void setSubentity(String subentity) {
		this.subentity = subentity;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setThevalue(String thevalue) {
		this.thevalue = thevalue;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
