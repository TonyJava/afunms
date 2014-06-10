package com.afunms.capreport.model;

public class ReportConfigNode {
	private String recieverNames;
	private String deviceNames;
	private String collectionOfGenerationTime;
	private String bidNames;
	private CycleReportConfig config;

	public String getRecieverNames() {
		return recieverNames;
	}

	public void setRecieverNames(String recieverNames) {
		this.recieverNames = recieverNames;
	}

	public String getDeviceNames() {
		return deviceNames;
	}

	public void setDeviceNames(String deviceNames) {
		this.deviceNames = deviceNames;
	}

	public String getBidNames() {
		return bidNames;
	}

	public void setBidNames(String bidNames) {
		this.bidNames = bidNames;
	}

	public String getCollectionOfGenerationTime() {
		return collectionOfGenerationTime;
	}

	public void setCollectionOfGenerationTime(String collectionOfGenerationTime) {
		this.collectionOfGenerationTime = collectionOfGenerationTime;
	}

	public CycleReportConfig getConfig() {
		return config;
	}

	public void setConfig(CycleReportConfig config) {
		this.config = config;
	}

}
