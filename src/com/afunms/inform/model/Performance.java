
package com.afunms.inform.model;

public class Performance {
	private String ipAddress;
	private String entity;
	private String id;
	private double value;

	public String getEntity() {
		return entity;
	}

	public String getId() {
		return id;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public double getValue() {
		return value;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setValue(double value) {
		this.value = value;
	}
}