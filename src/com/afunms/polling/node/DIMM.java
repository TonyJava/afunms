package com.afunms.polling.node;

public class DIMM {

	private String name;
	private String status;
	private String identification;
	private String capacity;

	public String getCapacity() {
		return capacity;
	}

	public String getIdentification() {
		return identification;
	}

	public String getName() {
		return name;
	}

	public String getStatus() {
		return status;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
