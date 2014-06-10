package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class CfgBaseInfo extends BaseVo {
	private int id;
	private String policyName;// ��������
	private String name; // ������
	private String value; // ֵ
	private String priority; // ����
	private String type; // ����
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
