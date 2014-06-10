package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppDisk extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	private String ipaddress;

	private String dfIndex;

	private String dfFileSys;

	private String dfKBytesTotal;

	private String dfKBytesUsed;

	private String dfKBytesAvail;

	private String dfPerCentKBytesCapacity;

	private String dfInodesUsed;

	private String dfInodesFree;

	private String dfPerCentInodeCapacity;

	private String dfMountedOn;

	private String dfMaxFilesAvail;

	private String dfMaxFilesUsed;

	private String dfMaxFilesPossible;

	private String dfHighTotalKBytes;

	private String dfLowTotalKBytes;

	private String dfHighUsedKBytes;

	private String dfLowUsedKBytes;

	private String dfHighAvailKBytes;

	private String dfLowAvailKBytes;

	private String dfStatus;

	private String dfMirrorStatus;

	private String dfPlexCount;

	private String dfType;

	private Calendar collectTime;

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getDfFileSys() {
		return dfFileSys;
	}

	public String getDfHighAvailKBytes() {
		return dfHighAvailKBytes;
	}

	public String getDfHighTotalKBytes() {
		return dfHighTotalKBytes;
	}

	public String getDfHighUsedKBytes() {
		return dfHighUsedKBytes;
	}

	public String getDfIndex() {
		return dfIndex;
	}

	public String getDfInodesFree() {
		return dfInodesFree;
	}

	public String getDfInodesUsed() {
		return dfInodesUsed;
	}

	public String getDfKBytesAvail() {
		return dfKBytesAvail;
	}

	public String getDfKBytesTotal() {
		return dfKBytesTotal;
	}

	public String getDfKBytesUsed() {
		return dfKBytesUsed;
	}

	public String getDfLowAvailKBytes() {
		return dfLowAvailKBytes;
	}

	public String getDfLowTotalKBytes() {
		return dfLowTotalKBytes;
	}

	public String getDfLowUsedKBytes() {
		return dfLowUsedKBytes;
	}

	public String getDfMaxFilesAvail() {
		return dfMaxFilesAvail;
	}

	public String getDfMaxFilesPossible() {
		return dfMaxFilesPossible;
	}

	public String getDfMaxFilesUsed() {
		return dfMaxFilesUsed;
	}

	public String getDfMirrorStatus() {
		return dfMirrorStatus;
	}

	public String getDfMountedOn() {
		return dfMountedOn;
	}

	public String getDfPerCentInodeCapacity() {
		return dfPerCentInodeCapacity;
	}

	public String getDfPerCentKBytesCapacity() {
		return dfPerCentKBytesCapacity;
	}

	public String getDfPlexCount() {
		return dfPlexCount;
	}

	public String getDfStatus() {
		return dfStatus;
	}

	public String getDfType() {
		return dfType;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setDfFileSys(String dfFileSys) {
		this.dfFileSys = dfFileSys;
	}

	public void setDfHighAvailKBytes(String dfHighAvailKBytes) {
		this.dfHighAvailKBytes = dfHighAvailKBytes;
	}

	public void setDfHighTotalKBytes(String dfHighTotalKBytes) {
		this.dfHighTotalKBytes = dfHighTotalKBytes;
	}

	public void setDfHighUsedKBytes(String dfHighUsedKBytes) {
		this.dfHighUsedKBytes = dfHighUsedKBytes;
	}

	public void setDfIndex(String dfIndex) {
		this.dfIndex = dfIndex;
	}

	public void setDfInodesFree(String dfInodesFree) {
		this.dfInodesFree = dfInodesFree;
	}

	public void setDfInodesUsed(String dfInodesUsed) {
		this.dfInodesUsed = dfInodesUsed;
	}

	public void setDfKBytesAvail(String dfKBytesAvail) {
		this.dfKBytesAvail = dfKBytesAvail;
	}

	public void setDfKBytesTotal(String dfKBytesTotal) {
		this.dfKBytesTotal = dfKBytesTotal;
	}

	public void setDfKBytesUsed(String dfKBytesUsed) {
		this.dfKBytesUsed = dfKBytesUsed;
	}

	public void setDfLowAvailKBytes(String dfLowAvailKBytes) {
		this.dfLowAvailKBytes = dfLowAvailKBytes;
	}

	public void setDfLowTotalKBytes(String dfLowTotalKBytes) {
		this.dfLowTotalKBytes = dfLowTotalKBytes;
	}

	public void setDfLowUsedKBytes(String dfLowUsedKBytes) {
		this.dfLowUsedKBytes = dfLowUsedKBytes;
	}

	public void setDfMaxFilesAvail(String dfMaxFilesAvail) {
		this.dfMaxFilesAvail = dfMaxFilesAvail;
	}

	public void setDfMaxFilesPossible(String dfMaxFilesPossible) {
		this.dfMaxFilesPossible = dfMaxFilesPossible;
	}

	public void setDfMaxFilesUsed(String dfMaxFilesUsed) {
		this.dfMaxFilesUsed = dfMaxFilesUsed;
	}

	public void setDfMirrorStatus(String dfMirrorStatus) {
		this.dfMirrorStatus = dfMirrorStatus;
	}

	public void setDfMountedOn(String dfMountedOn) {
		this.dfMountedOn = dfMountedOn;
	}

	public void setDfPerCentInodeCapacity(String dfPerCentInodeCapacity) {
		this.dfPerCentInodeCapacity = dfPerCentInodeCapacity;
	}

	public void setDfPerCentKBytesCapacity(String dfPerCentKBytesCapacity) {
		this.dfPerCentKBytesCapacity = dfPerCentKBytesCapacity;
	}

	public void setDfPlexCount(String dfPlexCount) {
		this.dfPlexCount = dfPlexCount;
	}

	public void setDfStatus(String dfStatus) {
		this.dfStatus = dfStatus;
	}

	public void setDfType(String dfType) {
		this.dfType = dfType;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

}
