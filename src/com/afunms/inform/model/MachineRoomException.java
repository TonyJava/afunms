
package com.afunms.inform.model;

import com.afunms.common.base.BaseVo;

public class MachineRoomException extends BaseVo {
	private String id;
	private String ipAddress;
	private String message;
	private int level;
	private int category;
	private String logTime;

	public int getCategory() {
		return category;
	}

	public String getId() {
		return id;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getLevel() {
		return level;
	}

	public String getLogTime() {
		return logTime;
	}

	public String getMessage() {
		return message;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
