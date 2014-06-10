package com.afunms.polling.node;

import java.util.List;

public class Enclosure {
	private String name;
	private String enclosureID;
	private String enclosureStatus;
	private String enclosureType;
	private String nodeWWN;
	private List<EnclosureFru> frus;

	public String getEnclosureID() {
		return enclosureID;
	}

	public String getEnclosureStatus() {
		return enclosureStatus;
	}

	public String getEnclosureType() {
		return enclosureType;
	}

	public List<EnclosureFru> getFrus() {
		return frus;
	}

	public String getName() {
		return name;
	}

	public String getNodeWWN() {
		return nodeWWN;
	}

	public void setEnclosureID(String enclosureID) {
		this.enclosureID = enclosureID;
	}

	public void setEnclosureStatus(String enclosureStatus) {
		this.enclosureStatus = enclosureStatus;
	}

	public void setEnclosureType(String enclosureType) {
		this.enclosureType = enclosureType;
	}

	public void setFrus(List<EnclosureFru> frus) {
		this.frus = frus;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNodeWWN(String nodeWWN) {
		this.nodeWWN = nodeWWN;
	}

}
