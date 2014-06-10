package com.afunms.system.vo;

import java.io.Serializable;

public class SyslogVo implements Serializable {

	private String priorityName;

	private String ctime;

	private String processName;

	private String eventid;

	private String bak;

	private String hostname;

	public String getBak() {
		return bak;
	}

	public String getCtime() {
		return ctime;
	}

	public String getEventid() {
		return eventid;
	}

	public String getHostname() {
		return hostname;
	}

	public String getPriorityName() {
		return priorityName;
	}

	public String getProcessName() {
		return processName;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public void setEventid(String eventid) {
		this.eventid = eventid;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setPriorityName(String priorityName) {
		this.priorityName = priorityName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

}