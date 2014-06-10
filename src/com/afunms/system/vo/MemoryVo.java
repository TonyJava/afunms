package com.afunms.system.vo;

public class MemoryVo {

	private String date;

	private String virtualMemory;

	private String physicalMemory;

	public String getDate() {
		return date;
	}

	public String getPhysicalMemory() {
		return physicalMemory;
	}

	public String getVirtualMemory() {
		return virtualMemory;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setPhysicalMemory(String physicalMemory) {
		this.physicalMemory = physicalMemory;
	}

	public void setVirtualMemory(String virtualMemory) {
		this.virtualMemory = virtualMemory;
	}

}