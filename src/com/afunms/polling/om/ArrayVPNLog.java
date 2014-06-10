package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNLog implements Serializable {

	private int id;

	private String ipaddress;

	private String type;

	private String subtype;

	private Calendar Collecttime;

	private int logNotificationsSent;

	private int logNotificationsEnabled;

	private int logMaxSeverity;

	private int logHistTableMaxLength;

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public int getLogHistTableMaxLength() {
		return logHistTableMaxLength;
	}

	public int getLogMaxSeverity() {
		return logMaxSeverity;
	}

	public int getLogNotificationsEnabled() {
		return logNotificationsEnabled;
	}

	public int getLogNotificationsSent() {
		return logNotificationsSent;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getType() {
		return type;
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

	public void setLogHistTableMaxLength(int logHistTableMaxLength) {
		this.logHistTableMaxLength = logHistTableMaxLength;
	}

	public void setLogMaxSeverity(int logMaxSeverity) {
		this.logMaxSeverity = logMaxSeverity;
	}

	public void setLogNotificationsEnabled(int logNotificationsEnabled) {
		this.logNotificationsEnabled = logNotificationsEnabled;
	}

	public void setLogNotificationsSent(int logNotificationsSent) {
		this.logNotificationsSent = logNotificationsSent;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setType(String type) {
		this.type = type;
	}

}
