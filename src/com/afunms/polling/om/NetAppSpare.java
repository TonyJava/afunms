package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppSpare extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private Calendar collectTime;

	private String spareIndex;

	private String spareDiskName;

	private String spareStatus;

	private String spareDiskId;

	private String spareScsiAdapter;

	private String spareScsiId;

	private String spareTotalMb;

	private String spareTotalBlocks;

	private String spareDiskPort;

	private String spareSecondaryDiskName;

	private String spareSecondaryDiskPort;

	private String spareShelf;

	private String spareBay;

	private String sparePool;

	private String spareSectorSize;

	private String spareDiskSerialNumber;

	private String spareDiskVendor;

	private String spareDiskModel;

	private String spareDiskFirmwareRevision;

	private String spareDiskRPM;

	private String spareDiskType;

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getSpareBay() {
		return spareBay;
	}

	public String getSpareDiskFirmwareRevision() {
		return spareDiskFirmwareRevision;
	}

	public String getSpareDiskId() {
		return spareDiskId;
	}

	public String getSpareDiskModel() {
		return spareDiskModel;
	}

	public String getSpareDiskName() {
		return spareDiskName;
	}

	public String getSpareDiskPort() {
		return spareDiskPort;
	}

	public String getSpareDiskRPM() {
		return spareDiskRPM;
	}

	public String getSpareDiskSerialNumber() {
		return spareDiskSerialNumber;
	}

	public String getSpareDiskType() {
		return spareDiskType;
	}

	public String getSpareDiskVendor() {
		return spareDiskVendor;
	}

	public String getSpareIndex() {
		return spareIndex;
	}

	public String getSparePool() {
		return sparePool;
	}

	public String getSpareScsiAdapter() {
		return spareScsiAdapter;
	}

	public String getSpareScsiId() {
		return spareScsiId;
	}

	public String getSpareSecondaryDiskName() {
		return spareSecondaryDiskName;
	}

	public String getSpareSecondaryDiskPort() {
		return spareSecondaryDiskPort;
	}

	public String getSpareSectorSize() {
		return spareSectorSize;
	}

	public String getSpareShelf() {
		return spareShelf;
	}

	public String getSpareStatus() {
		return spareStatus;
	}

	public String getSpareTotalBlocks() {
		return spareTotalBlocks;
	}

	public String getSpareTotalMb() {
		return spareTotalMb;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setSpareBay(String spareBay) {
		this.spareBay = spareBay;
	}

	public void setSpareDiskFirmwareRevision(String spareDiskFirmwareRevision) {
		this.spareDiskFirmwareRevision = spareDiskFirmwareRevision;
	}

	public void setSpareDiskId(String spareDiskId) {
		this.spareDiskId = spareDiskId;
	}

	public void setSpareDiskModel(String spareDiskModel) {
		this.spareDiskModel = spareDiskModel;
	}

	public void setSpareDiskName(String spareDiskName) {
		this.spareDiskName = spareDiskName;
	}

	public void setSpareDiskPort(String spareDiskPort) {
		this.spareDiskPort = spareDiskPort;
	}

	public void setSpareDiskRPM(String spareDiskRPM) {
		this.spareDiskRPM = spareDiskRPM;
	}

	public void setSpareDiskSerialNumber(String spareDiskSerialNumber) {
		this.spareDiskSerialNumber = spareDiskSerialNumber;
	}

	public void setSpareDiskType(String spareDiskType) {
		this.spareDiskType = spareDiskType;
	}

	public void setSpareDiskVendor(String spareDiskVendor) {
		this.spareDiskVendor = spareDiskVendor;
	}

	public void setSpareIndex(String spareIndex) {
		this.spareIndex = spareIndex;
	}

	public void setSparePool(String sparePool) {
		this.sparePool = sparePool;
	}

	public void setSpareScsiAdapter(String spareScsiAdapter) {
		this.spareScsiAdapter = spareScsiAdapter;
	}

	public void setSpareScsiId(String spareScsiId) {
		this.spareScsiId = spareScsiId;
	}

	public void setSpareSecondaryDiskName(String spareSecondaryDiskName) {
		this.spareSecondaryDiskName = spareSecondaryDiskName;
	}

	public void setSpareSecondaryDiskPort(String spareSecondaryDiskPort) {
		this.spareSecondaryDiskPort = spareSecondaryDiskPort;
	}

	public void setSpareSectorSize(String spareSectorSize) {
		this.spareSectorSize = spareSectorSize;
	}

	public void setSpareShelf(String spareShelf) {
		this.spareShelf = spareShelf;
	}

	public void setSpareStatus(String spareStatus) {
		this.spareStatus = spareStatus;
	}

	public void setSpareTotalBlocks(String spareTotalBlocks) {
		this.spareTotalBlocks = spareTotalBlocks;
	}

	public void setSpareTotalMb(String spareTotalMb) {
		this.spareTotalMb = spareTotalMb;
	}

}
