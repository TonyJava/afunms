package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class CfgCmdFile extends BaseVo {
	private int id;
	private String filename;
	private String createBy;
	private String createTime;
	private String fileDesc;
	private String devicetype;

	public String getCreateBy() {
		return createBy;
	}

	public String getCreateTime() {
		return createTime;
	}

	public String getDevicetype() {
		return devicetype;
	}

	public String getFileDesc() {
		return fileDesc;
	}

	public String getFilename() {
		return filename;
	}

	public int getId() {
		return id;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setId(int id) {
		this.id = id;
	}

}
