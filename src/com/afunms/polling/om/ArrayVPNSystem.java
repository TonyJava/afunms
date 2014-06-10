package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNSystem implements Serializable {

	private int id;

	private String ipaddress;

	private String type;

	private String subtype;

	private Calendar Collecttime;

	private int cpuUtilization;

	private int connectionsPerSec;

	private int requestsPerSec;

	private String sysDescr;

	private String sysObjectID;

	private String sysUpTime;

	private String sysContact;

	private String sysName;

	private String sysLocation;

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public int getConnectionsPerSec() {
		return connectionsPerSec;
	}

	public int getCpuUtilization() {
		return cpuUtilization;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public int getRequestsPerSec() {
		return requestsPerSec;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getSysContact() {
		return sysContact;
	}

	public String getSysDescr() {
		return sysDescr;
	}

	public String getSysLocation() {
		return sysLocation;
	}

	public String getSysName() {
		return sysName;
	}

	public String getSysObjectID() {
		return sysObjectID;
	}

	public String getSysUpTime() {
		return sysUpTime;
	}

	public String getType() {
		return type;
	}

	public void setCollecttime(Calendar collecttime) {
		Collecttime = collecttime;
	}

	public void setConnectionsPerSec(int connectionsPerSec) {
		this.connectionsPerSec = connectionsPerSec;
	}

	public void setCpuUtilization(int cpuUtilization) {
		this.cpuUtilization = cpuUtilization;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setRequestsPerSec(int requestsPerSec) {
		this.requestsPerSec = requestsPerSec;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setSysContact(String sysContact) {
		this.sysContact = sysContact;
	}

	public void setSysDescr(String sysDescr) {
		this.sysDescr = sysDescr;
	}

	public void setSysLocation(String sysLocation) {
		this.sysLocation = sysLocation;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public void setSysObjectID(String sysObjectID) {
		this.sysObjectID = sysObjectID;
	}

	public void setSysUpTime(String sysUpTime) {
		this.sysUpTime = sysUpTime;
	}

	public void setType(String type) {
		this.type = type;
	}

}
