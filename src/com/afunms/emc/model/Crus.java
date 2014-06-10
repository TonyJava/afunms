package com.afunms.emc.model;

import com.afunms.common.base.BaseVo;

public class Crus extends BaseVo {
	/**
	 * 
	 */
	private String nodeid;
	private String name;
	private String spStateStr;
	private String powerState;
	private String busLCC;
	private String bussps;
	private String busCabling;

	public String getBusCabling() {
		return busCabling;
	}

	public String getBusLCC() {
		return busLCC;
	}

	public String getBussps() {
		return bussps;
	}

	public String getName() {
		return name;
	}

	public String getNodeid() {
		return nodeid;
	}

	public String getPowerState() {
		return powerState;
	}

	public String getSpStateStr() {
		return spStateStr;
	}

	public void setBusCabling(String busCabling) {
		this.busCabling = busCabling;
	}

	public void setBusLCC(String busLCC) {
		this.busLCC = busLCC;
	}

	public void setBussps(String bussps) {
		this.bussps = bussps;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public void setPowerState(String powerState) {
		this.powerState = powerState;
	}

	public void setSpStateStr(String spStateStr) {
		this.spStateStr = spStateStr;
	}

	// private String lccStateStr;
	// private String cablingStateStr;
	// private Map<String,String> spState;
	// private Map<String,String> lccState;
	// private Map<String,String> cablingState;

}
