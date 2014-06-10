package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class GatherAcl extends BaseVo {
	private int id;
	private String ipaddress;
	private String command;
	private int isMonitor;

	public String getCommand() {
		return command;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public int getIsMonitor() {
		return isMonitor;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setIsMonitor(int isMonitor) {
		this.isMonitor = isMonitor;
	}

}
