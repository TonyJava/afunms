package com.afunms.inform.model;

import java.util.HashMap;


@SuppressWarnings("unchecked")
public class SystemSnap {
	private String networkClass;
	private String serverClass;
	private String databaseClass;
	private String linksClass;
	private String virusClass;
	private String roomStatusClass;
	private String governClientClass;
	private String internetClass;
	private String oaStatusClass;
	private String doorSystemClass;
	public HashMap urlsTbl;

	public SystemSnap() {
		urlsTbl = new HashMap();
	}

	public String getDatabaseClass() {
		return databaseClass;
	}

	public String getDoorSystemClass() {
		return doorSystemClass;
	}

	public String getGovernClientClass() {
		return governClientClass;
	}

	public String getInternetClass() {
		return internetClass;
	}

	public String getLinksClass() {
		return linksClass;
	}

	public String getNetworkClass() {
		return networkClass;
	}

	public String getOaStatusClass() {
		return oaStatusClass;
	}

	public String getRoomStatusClass() {
		return roomStatusClass;
	}

	public String getServerClass() {
		return serverClass;
	}

	public String getVirusClass() {
		return virusClass;
	}

	public void setDatabaseClass(String databaseClass) {
		this.databaseClass = databaseClass;
	}

	public void setDoorSystemClass(String doorSystemClass) {
		this.doorSystemClass = doorSystemClass;
	}

	public void setGovernClientClass(String governClientClass) {
		this.governClientClass = governClientClass;
	}

	public void setInternetClass(String internetClass) {
		this.internetClass = internetClass;
	}

	public void setLinksClass(String linksClass) {
		this.linksClass = linksClass;
	}

	public void setNetworkClass(String networkClass) {
		this.networkClass = networkClass;
	}

	public void setOaStatusClass(String oaStatusClass) {
		this.oaStatusClass = oaStatusClass;
	}

	public void setRoomStatusClass(String roomStatusClass) {
		this.roomStatusClass = roomStatusClass;
	}

	public void setServerClass(String serverClass) {
		this.serverClass = serverClass;
	}

	public void setVirusClass(String virusClass) {
		this.virusClass = virusClass;
	}
}
