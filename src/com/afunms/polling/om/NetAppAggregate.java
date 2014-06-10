package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppAggregate extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	private String ipaddress;

	private String aggrIndex;

	private String aggrName;

	private String aggrFSID;

	private String aggrOwningHost;

	private String aggrState;

	private String aggrStatus;

	private String aggrOptions;

	private String aggrUUID;

	private String aggrFlexvollist;

	private String aggrType;

	private String aggrRaidType;

	private Calendar collectTime;

	public String getAggrFlexvollist() {
		return aggrFlexvollist;
	}

	public String getAggrFSID() {
		return aggrFSID;
	}

	public String getAggrIndex() {
		return aggrIndex;
	}

	public String getAggrName() {
		return aggrName;
	}

	public String getAggrOptions() {
		return aggrOptions;
	}

	public String getAggrOwningHost() {
		return aggrOwningHost;
	}

	public String getAggrRaidType() {
		return aggrRaidType;
	}

	public String getAggrState() {
		return aggrState;
	}

	public String getAggrStatus() {
		return aggrStatus;
	}

	public String getAggrType() {
		return aggrType;
	}

	public String getAggrUUID() {
		return aggrUUID;
	}

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setAggrFlexvollist(String aggrFlexvollist) {
		this.aggrFlexvollist = aggrFlexvollist;
	}

	public void setAggrFSID(String aggrFSID) {
		this.aggrFSID = aggrFSID;
	}

	public void setAggrIndex(String aggrIndex) {
		this.aggrIndex = aggrIndex;
	}

	public void setAggrName(String aggrName) {
		this.aggrName = aggrName;
	}

	public void setAggrOptions(String aggrOptions) {
		this.aggrOptions = aggrOptions;
	}

	public void setAggrOwningHost(String aggrOwningHost) {
		this.aggrOwningHost = aggrOwningHost;
	}

	public void setAggrRaidType(String aggrRaidType) {
		this.aggrRaidType = aggrRaidType;
	}

	public void setAggrState(String aggrState) {
		this.aggrState = aggrState;
	}

	public void setAggrStatus(String aggrStatus) {
		this.aggrStatus = aggrStatus;
	}

	public void setAggrType(String aggrType) {
		this.aggrType = aggrType;
	}

	public void setAggrUUID(String aggrUUID) {
		this.aggrUUID = aggrUUID;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

}
