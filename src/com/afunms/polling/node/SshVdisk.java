package com.afunms.polling.node;

/**
 * 
 * @descrition ¥≈≈Ã’Û¡–
 * @author wangxiangyong
 * @date Jun 16, 2013 1:51:58 PM
 */
public class SshVdisk {
	private String name;
	private String size;
	private String free;
	private String own;
	private String pref;
	private String raid;
	private String disks;// ¥≈≈Ã ˝
	private String spr;
	private String chk;
	private String statusJobs;
	private String serialNumber;

	public String getChk() {
		return chk;
	}

	public String getDisks() {
		return disks;
	}

	public String getFree() {
		return free;
	}

	public String getName() {
		return name;
	}

	public String getOwn() {
		return own;
	}

	public String getPref() {
		return pref;
	}

	public String getRaid() {
		return raid;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getSize() {
		return size;
	}

	public String getSpr() {
		return spr;
	}

	public String getStatusJobs() {
		return statusJobs;
	}

	public void setChk(String chk) {
		this.chk = chk;
	}

	public void setDisks(String disks) {
		this.disks = disks;
	}

	public void setFree(String free) {
		this.free = free;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOwn(String own) {
		this.own = own;
	}

	public void setPref(String pref) {
		this.pref = pref;
	}

	public void setRaid(String raid) {
		this.raid = raid;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setSpr(String spr) {
		this.spr = spr;
	}

	public void setStatusJobs(String statusJobs) {
		this.statusJobs = statusJobs;
	}

}
