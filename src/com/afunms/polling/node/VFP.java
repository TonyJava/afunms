package com.afunms.polling.node;

public class VFP {

	private String name;
	private String VFPBaudRate;
	private String VFPPagingValue;

	public String getName() {
		return name;
	}

	public String getVFPBaudRate() {
		return VFPBaudRate;
	}

	public String getVFPPagingValue() {
		return VFPPagingValue;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVFPBaudRate(String baudRate) {
		VFPBaudRate = baudRate;
	}

	public void setVFPPagingValue(String pagingValue) {
		VFPPagingValue = pagingValue;
	}
}
