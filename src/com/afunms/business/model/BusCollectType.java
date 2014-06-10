package com.afunms.business.model;

import com.afunms.common.base.BaseVo;

public class BusCollectType extends BaseVo {

	private int id;

	private String collecttype;

	private String bct_desc;

	public String getBct_desc() {
		return bct_desc;
	}

	public void setBct_desc(String bct_desc) {
		this.bct_desc = bct_desc;
	}

	public String getCollecttype() {
		return collecttype;
	}

	public void setCollecttype(String collecttype) {
		this.collecttype = collecttype;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
