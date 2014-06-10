package com.afunms.system.vo;

public class AlarmVo {

	private String type_;

	private String type;

	private String name;

	private String ip;

	private String location;

	private String bussiness;

	private String level1;

	public String getBussiness() {
		return bussiness;
	}

	public String getIp() {
		return ip;
	}

	public String getLevel1() {
		return level1;
	}

	public String getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getType_() {
		return type_;
	}

	public void setBussiness(String bussiness) {
		this.bussiness = bussiness;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setLevel1(String level1) {
		this.level1 = level1;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setType_(String type_) {
		this.type_ = type_;
	}

}