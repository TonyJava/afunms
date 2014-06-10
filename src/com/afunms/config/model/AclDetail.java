package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class AclDetail extends BaseVo {
	private int id;
	private int baseId;
	private String name;
	private int value;
	private int matches;
	private String desc;
	private int status;
	private String collecttime;

	public int getBaseId() {
		return baseId;
	}

	public String getCollecttime() {
		return collecttime;
	}

	public String getDesc() {
		return desc;
	}

	public int getId() {
		return id;
	}

	public int getMatches() {
		return matches;
	}

	public String getName() {
		return name;
	}

	public int getStatus() {
		return status;
	}

	public int getValue() {
		return value;
	}

	public void setBaseId(int baseId) {
		this.baseId = baseId;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMatches(int matches) {
		this.matches = matches;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
