package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppVolume extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	private String ipaddress;

	private String volIndex;

	private String volName;

	private String volFSID;

	private String volOwningHost;

	private String volState;

	private String volStatus;

	private String volOptions;

	private String volUUID;

	private String volAggrName;

	private String volType;

	private Calendar collectTime;

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getVolAggrName() {
		return volAggrName;
	}

	public String getVolFSID() {
		return volFSID;
	}

	public String getVolIndex() {
		return volIndex;
	}

	public String getVolName() {
		return volName;
	}

	public String getVolOptions() {
		return volOptions;
	}

	public String getVolOwningHost() {
		return volOwningHost;
	}

	public String getVolState() {
		return volState;
	}

	public String getVolStatus() {
		return volStatus;
	}

	public String getVolType() {
		return volType;
	}

	public String getVolUUID() {
		return volUUID;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setVolAggrName(String volAggrName) {
		this.volAggrName = volAggrName;
	}

	public void setVolFSID(String volFSID) {
		this.volFSID = volFSID;
	}

	public void setVolIndex(String volIndex) {
		this.volIndex = volIndex;
	}

	public void setVolName(String volName) {
		this.volName = volName;
	}

	public void setVolOptions(String volOptions) {
		this.volOptions = volOptions;
	}

	public void setVolOwningHost(String volOwningHost) {
		this.volOwningHost = volOwningHost;
	}

	public void setVolState(String volState) {
		this.volState = volState;
	}

	public void setVolStatus(String volStatus) {
		this.volStatus = volStatus;
	}

	public void setVolType(String volType) {
		this.volType = volType;
	}

	public void setVolUUID(String volUUID) {
		this.volUUID = volUUID;
	}

}
