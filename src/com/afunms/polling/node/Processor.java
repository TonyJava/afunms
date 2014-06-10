package com.afunms.polling.node;

public class Processor {
	private String name;
	private String status;
	private String identification;

	public String getIdentification() {
		return identification;
	}

	public String getName() {
		return name;
	}

	public String getStatus() {
		return status;
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
