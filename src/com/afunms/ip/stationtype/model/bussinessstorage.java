package com.afunms.ip.stationtype.model;

import com.afunms.common.base.BaseVo;

/**
 * @author Administrator
 * 
 */
public class bussinessstorage extends BaseVo {

	private int id;
	private String bussiness;
	private int type;
	private int bussiness_id;
	private String field_id;
	private String bus_id;

	public String getBus_id() {
		return bus_id;
	}

	public String getBussiness() {
		return bussiness;
	}

	public int getBussiness_id() {
		return bussiness_id;
	}

	public String getField_id() {
		return field_id;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public void setBus_id(String bus_id) {
		this.bus_id = bus_id;
	}

	public void setBussiness(String bussiness) {
		this.bussiness = bussiness;
	}

	public void setBussiness_id(int bussiness_id) {
		this.bussiness_id = bussiness_id;
	}

	public void setField_id(String field_id) {
		this.field_id = field_id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setType(int type) {
		this.type = type;
	}
}
