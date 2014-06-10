package com.afunms.capreport.model;

import com.afunms.common.base.BaseVo;

public class CycleReportConfig extends BaseVo {
	private int id;
	private String collectionOfRecieverId;
	private String bids;
	private String collectionOfdeviceId;
	private String collectionOfGenerationTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCollectionOfRecieverId() {
		return collectionOfRecieverId;
	}

	public void setCollectionOfRecieverId(String collectionOfRecieverId) {
		this.collectionOfRecieverId = collectionOfRecieverId;
	}

	public String getBids() {
		return bids;
	}

	public void setBids(String bids) {
		this.bids = bids;
	}

	public String getCollectionOfdeviceId() {
		return collectionOfdeviceId;
	}

	public void setCollectionOfdeviceId(String collectionOfdeviceId) {
		this.collectionOfdeviceId = collectionOfdeviceId;
	}

	public String getCollectionOfGenerationTime() {
		return collectionOfGenerationTime;
	}

	public void setCollectionOfGenerationTime(String collectionOfGenerationTime) {
		this.collectionOfGenerationTime = collectionOfGenerationTime;
	}
}
