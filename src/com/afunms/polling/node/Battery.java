package com.afunms.polling.node;

public class Battery {
	private String name;
	private String status;
	private String identification;
	private String manufacturerName;
	private String deviceName;
	private String manufacturerDate;
	private String remainingCapacity;
	private String pctRemainingCapacity;
	private String voltage;
	private String dischargeCycles;

	public String getDeviceName() {
		return deviceName;
	}

	public String getDischargeCycles() {
		return dischargeCycles;
	}

	public String getIdentification() {
		return identification;
	}

	public String getManufacturerDate() {
		return manufacturerDate;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public String getName() {
		return name;
	}

	public String getPctRemainingCapacity() {
		return pctRemainingCapacity;
	}

	public String getRemainingCapacity() {
		return remainingCapacity;
	}

	public String getStatus() {
		return status;
	}

	public String getVoltage() {
		return voltage;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public void setDischargeCycles(String dischargeCycles) {
		this.dischargeCycles = dischargeCycles;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public void setManufacturerDate(String manufacturerDate) {
		this.manufacturerDate = manufacturerDate;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPctRemainingCapacity(String pctRemainingCapacity) {
		this.pctRemainingCapacity = pctRemainingCapacity;
	}

	public void setRemainingCapacity(String remainingCapacity) {
		this.remainingCapacity = remainingCapacity;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setVoltage(String voltage) {
		this.voltage = voltage;
	}

}
