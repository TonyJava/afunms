package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class VpnCfgCmdFile extends BaseVo {
	private int id;
	private String name;
	private String filename;
	private String createBy;
	private String createTime;
	private String fileDesc;
	private String vpnType;
	private String deviceType;

	public String getCreateBy() {
		return createBy;
	}

	public String getCreateTime() {
		return createTime;
	}

	public String getDeviceType() {
		return deviceType;
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

	public String getName() {
		return name;
	}

	public String getVpnType() {
		return vpnType;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
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

	public void setName(String name) {
		this.name = name;
	}

	public void setVpnType(String vpnType) {
		this.vpnType = vpnType;
	}

}
