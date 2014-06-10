
package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class StorageTypeVo extends BaseVo {
	private int id;
	private int producer;
	private String model;
	private String descr;

	public int getProducer() {
		return producer;
	}

	public void setProducer(int producer) {
		this.producer = producer;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
}