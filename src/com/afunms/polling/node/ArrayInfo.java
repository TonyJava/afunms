package com.afunms.polling.node;

import java.util.Map;

public class ArrayInfo {
	private String arrayStatus;
	private String firmwareRevision;
	private String productRevision;
	private String localControllerProductRevision;
	private String remoteControllerProductRevision;
	private Map<String, String> lastEventLogEntry;

	public String getArrayStatus() {
		return arrayStatus;
	}

	public String getFirmwareRevision() {
		return firmwareRevision;
	}

	public Map<String, String> getLastEventLogEntry() {
		return lastEventLogEntry;
	}

	public String getLocalControllerProductRevision() {
		return localControllerProductRevision;
	}

	public String getProductRevision() {
		return productRevision;
	}

	public String getRemoteControllerProductRevision() {
		return remoteControllerProductRevision;
	}

	public void setArrayStatus(String arrayStatus) {
		this.arrayStatus = arrayStatus;
	}

	public void setFirmwareRevision(String firmwareRevision) {
		this.firmwareRevision = firmwareRevision;
	}

	public void setLastEventLogEntry(Map<String, String> lastEventLogEntry) {
		this.lastEventLogEntry = lastEventLogEntry;
	}

	public void setLocalControllerProductRevision(String localControllerProductRevision) {
		this.localControllerProductRevision = localControllerProductRevision;
	}

	public void setProductRevision(String productRevision) {
		this.productRevision = productRevision;
	}

	public void setRemoteControllerProductRevision(String remoteControllerProductRevision) {
		this.remoteControllerProductRevision = remoteControllerProductRevision;
	}

}
