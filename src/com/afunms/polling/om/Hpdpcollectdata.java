package com.afunms.polling.om;

import com.afunms.common.base.BaseVo;

public class Hpdpcollectdata extends BaseVo {
	private int id;
	private String ipaddress;
	private String sessionId;
	private String type;
	private String status;
	private String userGroup;
	private String collecttime;

	public String getCollecttime() {
		return collecttime;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getStatus() {
		return status;
	}

	public String getType() {
		return type;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}
}
