package com.afunms.ip.stationtype.model;

import com.afunms.common.base.BaseVo;

public class loopbackstorage extends BaseVo {

	private int id;
	private String loopback;
	private int type;
	private int backbone_id;
	private int field_id;

	public int getBackbone_id() {
		return backbone_id;
	}

	public int getField_id() {
		return field_id;
	}

	public int getId() {
		return id;
	}

	public String getLoopback() {
		return loopback;
	}

	public int getType() {
		return type;
	}

	public void setBackbone_id(int backbone_id) {
		this.backbone_id = backbone_id;
	}

	public void setField_id(int field_id) {
		this.field_id = field_id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLoopback(String loopback) {
		this.loopback = loopback;
	}

	public void setType(int type) {
		this.type = type;
	}

}
