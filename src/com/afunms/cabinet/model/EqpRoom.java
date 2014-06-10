package com.afunms.cabinet.model;

import com.afunms.common.base.BaseVo;

public class EqpRoom extends BaseVo {

	private int id;
	private String name;
	private String descr;
	private String bak;
	private int operId;
	private String icon = "../resource/image/menu/jfpz.gif";

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getBak() {
		return bak;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public int getOperId() {
		return operId;
	}

	public void setOperId(int operId) {
		this.operId = operId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
