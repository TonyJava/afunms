package com.afunms.emc.model;

import java.util.List;

import com.afunms.common.base.BaseVo;

public class Environment extends BaseVo {
	private Array array;
	private List<MemModel> memList;
	private List<MemModel> bakPowerList;

	public Array getArray() {
		return array;
	}

	public List<MemModel> getBakPowerList() {
		return bakPowerList;
	}

	public List<MemModel> getMemList() {
		return memList;
	}

	public void setArray(Array array) {
		this.array = array;
	}

	public void setBakPowerList(List<MemModel> bakPowerList) {
		this.bakPowerList = bakPowerList;
	}

	public void setMemList(List<MemModel> memList) {
		this.memList = memList;
	}
}
