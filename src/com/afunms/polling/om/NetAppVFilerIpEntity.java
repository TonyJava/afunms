package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppVFilerIpEntity extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private Calendar collectTime;

	private String vfFiIndex;

	private String vfIpIndex;

	private String vfIpAddr;

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getVfFiIndex() {
		return vfFiIndex;
	}

	public String getVfIpAddr() {
		return vfIpAddr;
	}

	public String getVfIpIndex() {
		return vfIpIndex;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setVfFiIndex(String vfFiIndex) {
		this.vfFiIndex = vfFiIndex;
	}

	public void setVfIpAddr(String vfIpAddr) {
		this.vfIpAddr = vfIpAddr;
	}

	public void setVfIpIndex(String vfIpIndex) {
		this.vfIpIndex = vfIpIndex;
	}

}
