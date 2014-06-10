package com.afunms.polling.om;

import com.afunms.common.base.BaseVo;

public class Ggscicollectdata extends BaseVo {
	private int id;
	private String ipaddress;
	private String programName;
	private String status;
	private String group;
	private String lagAtChkpt;
	private String timeSinceChkpt;

	public String getGroup() {
		return group;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getLagAtChkpt() {
		return lagAtChkpt;
	}

	public String getProgramName() {
		return programName;
	}

	public String getStatus() {
		return status;
	}

	public String getTimeSinceChkpt() {
		return timeSinceChkpt;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setLagAtChkpt(String lagAtChkpt) {
		this.lagAtChkpt = lagAtChkpt;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTimeSinceChkpt(String timeSinceChkpt) {
		this.timeSinceChkpt = timeSinceChkpt;
	}
}
