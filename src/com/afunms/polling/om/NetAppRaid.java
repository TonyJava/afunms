package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppRaid extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private Calendar collectTime;

	private String raidVIndex;

	private String raidVDiskName;

	private String raidVStatus;

	private String raidVDiskId;

	private String raidVScsiAdapter;

	private String raidVScsiId;

	private String raidVUsedMb;

	private String raidVUsedBlocks;

	private String raidVTotalMb;

	private String raidVTotalBlocks;

	private String raidVCompletionPerCent;

	private String raidVVol;

	private String raidVGroup;

	private String raidVDiskNumber;

	private String raidVGroupNumber;

	private String raidVDiskPort;

	private String raidVSecondaryDiskName;

	private String raidVSecondaryDiskPort;

	private String raidVShelf;

	private String raidVBay;

	private String raidVPlex;

	private String raidVPlexGroup;

	private String raidVPlexNumber;

	private String raidVPlexName;

	private String raidVSectorSize; // ¿é´óÐ¡

	private String raidVDiskSerialNumber;

	private String raidVDiskVendor;

	private String raidVDiskModel;

	private String raidVDiskFirmwareRevision;

	private String raidVDiskRPM;

	private String raidVDiskType;

	private String raidVDiskPool;

	private String raidVDiskCopyDestDiskName;

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getRaidVBay() {
		return raidVBay;
	}

	public String getRaidVCompletionPerCent() {
		return raidVCompletionPerCent;
	}

	public String getRaidVDiskCopyDestDiskName() {
		return raidVDiskCopyDestDiskName;
	}

	public String getRaidVDiskFirmwareRevision() {
		return raidVDiskFirmwareRevision;
	}

	public String getRaidVDiskId() {
		return raidVDiskId;
	}

	public String getRaidVDiskModel() {
		return raidVDiskModel;
	}

	public String getRaidVDiskName() {
		return raidVDiskName;
	}

	public String getRaidVDiskNumber() {
		return raidVDiskNumber;
	}

	public String getRaidVDiskPool() {
		return raidVDiskPool;
	}

	public String getRaidVDiskPort() {
		return raidVDiskPort;
	}

	public String getRaidVDiskRPM() {
		return raidVDiskRPM;
	}

	public String getRaidVDiskSerialNumber() {
		return raidVDiskSerialNumber;
	}

	public String getRaidVDiskType() {
		return raidVDiskType;
	}

	public String getRaidVDiskVendor() {
		return raidVDiskVendor;
	}

	public String getRaidVGroup() {
		return raidVGroup;
	}

	public String getRaidVGroupNumber() {
		return raidVGroupNumber;
	}

	public String getRaidVIndex() {
		return raidVIndex;
	}

	public String getRaidVPlex() {
		return raidVPlex;
	}

	public String getRaidVPlexGroup() {
		return raidVPlexGroup;
	}

	public String getRaidVPlexName() {
		return raidVPlexName;
	}

	public String getRaidVPlexNumber() {
		return raidVPlexNumber;
	}

	public String getRaidVScsiAdapter() {
		return raidVScsiAdapter;
	}

	public String getRaidVScsiId() {
		return raidVScsiId;
	}

	public String getRaidVSecondaryDiskName() {
		return raidVSecondaryDiskName;
	}

	public String getRaidVSecondaryDiskPort() {
		return raidVSecondaryDiskPort;
	}

	public String getRaidVSectorSize() {
		return raidVSectorSize;
	}

	public String getRaidVShelf() {
		return raidVShelf;
	}

	public String getRaidVStatus() {
		return raidVStatus;
	}

	public String getRaidVTotalBlocks() {
		return raidVTotalBlocks;
	}

	public String getRaidVTotalMb() {
		return raidVTotalMb;
	}

	public String getRaidVUsedBlocks() {
		return raidVUsedBlocks;
	}

	public String getRaidVUsedMb() {
		return raidVUsedMb;
	}

	public String getRaidVVol() {
		return raidVVol;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setRaidVBay(String raidVBay) {
		this.raidVBay = raidVBay;
	}

	public void setRaidVCompletionPerCent(String raidVCompletionPerCent) {
		this.raidVCompletionPerCent = raidVCompletionPerCent;
	}

	public void setRaidVDiskCopyDestDiskName(String raidVDiskCopyDestDiskName) {
		this.raidVDiskCopyDestDiskName = raidVDiskCopyDestDiskName;
	}

	public void setRaidVDiskFirmwareRevision(String raidVDiskFirmwareRevision) {
		this.raidVDiskFirmwareRevision = raidVDiskFirmwareRevision;
	}

	public void setRaidVDiskId(String raidVDiskId) {
		this.raidVDiskId = raidVDiskId;
	}

	public void setRaidVDiskModel(String raidVDiskModel) {
		this.raidVDiskModel = raidVDiskModel;
	}

	public void setRaidVDiskName(String raidVDiskName) {
		this.raidVDiskName = raidVDiskName;
	}

	public void setRaidVDiskNumber(String raidVDiskNumber) {
		this.raidVDiskNumber = raidVDiskNumber;
	}

	public void setRaidVDiskPool(String raidVDiskPool) {
		this.raidVDiskPool = raidVDiskPool;
	}

	public void setRaidVDiskPort(String raidVDiskPort) {
		this.raidVDiskPort = raidVDiskPort;
	}

	public void setRaidVDiskRPM(String raidVDiskRPM) {
		this.raidVDiskRPM = raidVDiskRPM;
	}

	public void setRaidVDiskSerialNumber(String raidVDiskSerialNumber) {
		this.raidVDiskSerialNumber = raidVDiskSerialNumber;
	}

	public void setRaidVDiskType(String raidVDiskType) {
		this.raidVDiskType = raidVDiskType;
	}

	public void setRaidVDiskVendor(String raidVDiskVendor) {
		this.raidVDiskVendor = raidVDiskVendor;
	}

	public void setRaidVGroup(String raidVGroup) {
		this.raidVGroup = raidVGroup;
	}

	public void setRaidVGroupNumber(String raidVGroupNumber) {
		this.raidVGroupNumber = raidVGroupNumber;
	}

	public void setRaidVIndex(String raidVIndex) {
		this.raidVIndex = raidVIndex;
	}

	public void setRaidVPlex(String raidVPlex) {
		this.raidVPlex = raidVPlex;
	}

	public void setRaidVPlexGroup(String raidVPlexGroup) {
		this.raidVPlexGroup = raidVPlexGroup;
	}

	public void setRaidVPlexName(String raidVPlexName) {
		this.raidVPlexName = raidVPlexName;
	}

	public void setRaidVPlexNumber(String raidVPlexNumber) {
		this.raidVPlexNumber = raidVPlexNumber;
	}

	public void setRaidVScsiAdapter(String raidVScsiAdapter) {
		this.raidVScsiAdapter = raidVScsiAdapter;
	}

	public void setRaidVScsiId(String raidVScsiId) {
		this.raidVScsiId = raidVScsiId;
	}

	public void setRaidVSecondaryDiskName(String raidVSecondaryDiskName) {
		this.raidVSecondaryDiskName = raidVSecondaryDiskName;
	}

	public void setRaidVSecondaryDiskPort(String raidVSecondaryDiskPort) {
		this.raidVSecondaryDiskPort = raidVSecondaryDiskPort;
	}

	public void setRaidVSectorSize(String raidVSectorSize) {
		this.raidVSectorSize = raidVSectorSize;
	}

	public void setRaidVShelf(String raidVShelf) {
		this.raidVShelf = raidVShelf;
	}

	public void setRaidVStatus(String raidVStatus) {
		this.raidVStatus = raidVStatus;
	}

	public void setRaidVTotalBlocks(String raidVTotalBlocks) {
		this.raidVTotalBlocks = raidVTotalBlocks;
	}

	public void setRaidVTotalMb(String raidVTotalMb) {
		this.raidVTotalMb = raidVTotalMb;
	}

	public void setRaidVUsedBlocks(String raidVUsedBlocks) {
		this.raidVUsedBlocks = raidVUsedBlocks;
	}

	public void setRaidVUsedMb(String raidVUsedMb) {
		this.raidVUsedMb = raidVUsedMb;
	}

	public void setRaidVVol(String raidVVol) {
		this.raidVVol = raidVVol;
	}

}
