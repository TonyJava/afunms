package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNVIPData implements Serializable {

	private int id;

	private String ipaddress;

	private String type;

	private String subtype;

	private Calendar Collecttime;

	private int vipStatus;

	private String hostName;

	private String currentTime;

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public String getHostName() {
		return hostName;
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

	public int getVipStatus() {
		return vipStatus;
	}

	public void setCollecttime(Calendar collecttime) {
		Collecttime = collecttime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
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

	public void setVipStatus(int vipStatus) {
		this.vipStatus = vipStatus;
	}
}
