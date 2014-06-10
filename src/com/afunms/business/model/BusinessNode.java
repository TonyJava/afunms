package com.afunms.business.model;

import com.afunms.common.base.BaseVo;

public class BusinessNode extends BaseVo {

	private int id;

	private int bid;

	private String desc;

	private int collecttype;

	private String method;

	private String name;

	private int flag;

	public int getBid() {
		return bid;
	}

	public void setBid(int bid) {
		this.bid = bid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getCollecttype() {
		return collecttype;
	}

	public void setCollecttype(int collecttype) {
		this.collecttype = collecttype;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

}
