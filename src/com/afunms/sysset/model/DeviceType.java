package com.afunms.sysset.model;

import com.afunms.common.base.BaseVo;

public class DeviceType extends BaseVo {
	private int id;
	private String sysOid;
	private String descr;
	private String image;
	private int producer;
	private int category;
	private String locate;
	private String logTime;

	public DeviceType() {
		descr = null;
		image = null;
		locate = null;
		logTime = null;
	}

	public int getCategory() {
		return category;
	}

	public String getDescr() {
		return descr;
	}

	public int getId() {
		return id;
	}

	public String getImage() {
		return image;
	}

	public String getLocate() {
		return locate;
	}

	public String getLogTime() {
		return logTime;
	}

	public int getProducer() {
		return producer;
	}

	public String getSysOid() {
		return sysOid;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setLocate(String locate) {
		this.locate = locate;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	public void setProducer(int producer) {
		this.producer = producer;
	}

	public void setSysOid(String sysOid) {
		this.sysOid = sysOid;
	}
}
