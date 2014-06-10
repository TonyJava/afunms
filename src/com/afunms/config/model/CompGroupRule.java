package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class CompGroupRule extends BaseVo {
	private int id;
	private String name;
	private String description;
	private String deviceType;
	private String ruleId;
	private String createdBy;
	private String createdTime;
	private String lastModifiedBy;
	private String lastModifiedTime;

	public String getCreatedBy() {
		return createdBy;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public String getDescription() {
		return description;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public int getId() {
		return id;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public String getLastModifiedTime() {
		return lastModifiedTime;
	}

	public String getName() {
		return name;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public void setLastModifiedTime(String lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

}
