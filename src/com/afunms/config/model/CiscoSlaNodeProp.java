package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class CiscoSlaNodeProp extends BaseVo {
	private int id;
	private int telnetconfigid;
	private int entrynumber;
	private String createBy;
	private String slatype;
	private String createTime;
	private String bak;

	public String getBak() {
		return bak;
	}

	public String getCreateBy() {
		return createBy;
	}

	public String getCreateTime() {
		return createTime;
	}

	public int getEntrynumber() {
		return entrynumber;
	}

	public int getId() {
		return id;
	}

	public String getSlatype() {
		return slatype;
	}

	public int getTelnetconfigid() {
		return telnetconfigid;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public void setEntrynumber(int entrynumber) {
		this.entrynumber = entrynumber;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setSlatype(String slatype) {
		this.slatype = slatype;
	}

	public void setTelnetconfigid(int telnetconfigid) {
		this.telnetconfigid = telnetconfigid;
	}

}
