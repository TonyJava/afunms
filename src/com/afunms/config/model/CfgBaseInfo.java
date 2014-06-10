package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class CfgBaseInfo extends BaseVo {
	private int id;
	private String policyName;// 策略名称
	private String name; // 类名称
	private String value; // 值
	private String priority; // 级别
	private String type; // 类型
	private String collecttime;

	public String getCollecttime() {
		return collecttime;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPolicyName() {
		return policyName;
	}

	public String getPriority() {
		return priority;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
