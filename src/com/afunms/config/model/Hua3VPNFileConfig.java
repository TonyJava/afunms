package com.afunms.config.model;

import java.sql.Timestamp;

import com.afunms.common.base.BaseVo;

public class Hua3VPNFileConfig extends BaseVo {
	private int id;
	private String ipaddress;
	private String fileName;
	private String descri;
	private int fileSize;
	private Timestamp backupTime;
	private String bkpType;
	private int baseline;

	public Timestamp getBackupTime() {
		return backupTime;
	}

	public int getBaseline() {
		return baseline;
	}

	public String getBkpType() {
		return bkpType;
	}

	public String getDescri() {
		return descri;
	}

	public String getFileName() {
		return fileName;
	}

	public int getFileSize() {
		return fileSize;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setBackupTime(Timestamp backupTime) {
		this.backupTime = backupTime;
	}

	public void setBaseline(int baseline) {
		this.baseline = baseline;
	}

	public void setBkpType(String bkpType) {
		this.bkpType = bkpType;
	}

	public void setDescri(String descri) {
		this.descri = descri;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

}
