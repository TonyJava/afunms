package com.afunms.ip.stationtype.model;

import com.afunms.common.base.BaseVo;

public class pe_cestorage extends BaseVo {

	private int id;
	private String s1;
	private String s2;
	private String r_s1;
	private String r_s2;
	private int field_id;
	private int backbone_id;

	public int getBackbone_id() {
		return backbone_id;
	}

	public int getField_id() {
		return field_id;
	}

	public int getId() {
		return id;
	}

	public String getR_s1() {
		return r_s1;
	}

	public String getR_s2() {
		return r_s2;
	}

	public String getS1() {
		return s1;
	}

	public String getS2() {
		return s2;
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

	public void setR_s1(String r_s1) {
		this.r_s1 = r_s1;
	}

	public void setR_s2(String r_s2) {
		this.r_s2 = r_s2;
	}

	public void setS1(String s1) {
		this.s1 = s1;
	}

	public void setS2(String s2) {
		this.s2 = s2;
	}

}
