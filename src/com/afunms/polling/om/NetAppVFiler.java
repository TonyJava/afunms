package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppVFiler extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private Calendar collectTime;

	private String vfIndex;

	private String vfName;

	private String vfUuid;

	private String vfIpAddresses;

	private String vfStoragePaths;

	private String vfIpSpace; // 

	private String vfAllowedProtocols; // 

	private String vfDisallowedProtocols;// 

	private String vfState;// 

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getVfAllowedProtocols() {
		return vfAllowedProtocols;
	}

	public String getVfDisallowedProtocols() {
		return vfDisallowedProtocols;
	}

	public String getVfIndex() {
		return vfIndex;
	}

	public String getVfIpAddresses() {
		return vfIpAddresses;
	}

	public String getVfIpSpace() {
		return vfIpSpace;
	}

	public String getVfName() {
		return vfName;
	}

	public String getVfState() {
		return vfState;
	}

	public String getVfStoragePaths() {
		return vfStoragePaths;
	}

	public String getVfUuid() {
		return vfUuid;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setVfAllowedProtocols(String vfAllowedProtocols) {
		this.vfAllowedProtocols = vfAllowedProtocols;
	}

	public void setVfDisallowedProtocols(String vfDisallowedProtocols) {
		this.vfDisallowedProtocols = vfDisallowedProtocols;
	}

	public void setVfIndex(String vfIndex) {
		this.vfIndex = vfIndex;
	}

	public void setVfIpAddresses(String vfIpAddresses) {
		this.vfIpAddresses = vfIpAddresses;
	}

	public void setVfIpSpace(String vfIpSpace) {
		this.vfIpSpace = vfIpSpace;
	}

	public void setVfName(String vfName) {
		this.vfName = vfName;
	}

	public void setVfState(String vfState) {
		this.vfState = vfState;
	}

	public void setVfStoragePaths(String vfStoragePaths) {
		this.vfStoragePaths = vfStoragePaths;
	}

	public void setVfUuid(String vfUuid) {
		this.vfUuid = vfUuid;
	}

}
