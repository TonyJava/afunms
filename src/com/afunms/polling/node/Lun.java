package com.afunms.polling.node;

public class Lun {
	private String name;
	private String redundancyGroup;
	private String active;
	private String dataCapacity;
	private String wwn;
	private String numberOfBusinessCopies;

	public String getActive() {
		return active;
	}

	public String getDataCapacity() {
		return dataCapacity;
	}

	public String getName() {
		return name;
	}

	public String getNumberOfBusinessCopies() {
		return numberOfBusinessCopies;
	}

	public String getRedundancyGroup() {
		return redundancyGroup;
	}

	public String getWwn() {
		return wwn;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public void setDataCapacity(String dataCapacity) {
		this.dataCapacity = dataCapacity;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNumberOfBusinessCopies(String numberOfBusinessCopies) {
		this.numberOfBusinessCopies = numberOfBusinessCopies;
	}

	public void setRedundancyGroup(String redundancyGroup) {
		this.redundancyGroup = redundancyGroup;
	}

	public void setWwn(String wwn) {
		this.wwn = wwn;
	}

}
