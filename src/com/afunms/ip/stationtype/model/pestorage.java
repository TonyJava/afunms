package com.afunms.ip.stationtype.model;

import com.afunms.common.base.BaseVo;

public class pestorage extends BaseVo {

	private int id;
	private String backbone1;
	private String backbone2;
	private int field_id;
	private int backbone_id;

	public int getBackbone_id() {
		return backbone_id;
	}

	public String getBackbone1() {
		return backbone1;
	}

	public String getBackbone2() {
		return backbone2;
	}

	public int getField_id() {
		return field_id;
	}

	public int getId() {
		return id;
	}

	public void setBackbone_id(int backbone_id) {
		this.backbone_id = backbone_id;
	}

	public void setBackbone1(String backbone1) {
		this.backbone1 = backbone1;
	}

	public void setBackbone2(String backbone2) {
		this.backbone2 = backbone2;
	}

	public void setField_id(int field_id) {
		this.field_id = field_id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
