package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNVS implements Serializable {

	private int id;

	private String ipaddress;

	private String type;

	private String subtype;

	private Calendar Collecttime;

	private int vsIndex;

	private String vsID;

	private int vsProtocol;

	private String vsIpAddr;

	private int vsPort;

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getType() {
		return type;
	}

	public String getVsID() {
		return vsID;
	}

	public int getVsIndex() {
		return vsIndex;
	}

	public String getVsIpAddr() {
		return vsIpAddr;
	}

	public int getVsPort() {
		return vsPort;
	}

	public int getVsProtocol() {
		return vsProtocol;
	}

	public void setCollecttime(Calendar collecttime) {
		Collecttime = collecttime;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setVsID(String vsID) {
		this.vsID = vsID;
	}

	public void setVsIndex(int vsIndex) {
		this.vsIndex = vsIndex;
	}

	public void setVsIpAddr(String vsIpAddr) {
		this.vsIpAddr = vsIpAddr;
	}

	public void setVsPort(int vsPort) {
		this.vsPort = vsPort;
	}

	public void setVsProtocol(int vsProtocol) {
		this.vsProtocol = vsProtocol;
	}

}
