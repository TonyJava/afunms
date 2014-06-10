package com.afunms.polling.node;

public class Disk {
	private String name;
	private String Status;
	private String diskState;
	private String vendorID;
	private String productID;
	private String productRevision;
	private String dataCapacity;
	private String blockLength;
	private String address;
	private String nodeWWN;
	private String initializeState;
	private String redundancyGroup;
	private String volumeSetSerialNumber;
	private String serialNumber;
	private String firmwareRevision;

	public String getAddress() {
		return address;
	}

	public String getBlockLength() {
		return blockLength;
	}

	public String getDataCapacity() {
		return dataCapacity;
	}

	public String getDiskState() {
		return diskState;
	}

	public String getFirmwareRevision() {
		return firmwareRevision;
	}

	public String getInitializeState() {
		return initializeState;
	}

	public String getName() {
		return name;
	}

	public String getNodeWWN() {
		return nodeWWN;
	}

	public String getProductID() {
		return productID;
	}

	public String getProductRevision() {
		return productRevision;
	}

	public String getRedundancyGroup() {
		return redundancyGroup;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getStatus() {
		return Status;
	}

	public String getVendorID() {
		return vendorID;
	}

	public String getVolumeSetSerialNumber() {
		return volumeSetSerialNumber;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setBlockLength(String blockLength) {
		this.blockLength = blockLength;
	}

	public void setDataCapacity(String dataCapacity) {
		this.dataCapacity = dataCapacity;
	}

	public void setDiskState(String diskState) {
		this.diskState = diskState;
	}

	public void setFirmwareRevision(String firmwareRevision) {
		this.firmwareRevision = firmwareRevision;
	}

	public void setInitializeState(String initializeState) {
		this.initializeState = initializeState;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNodeWWN(String nodeWWN) {
		this.nodeWWN = nodeWWN;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public void setProductRevision(String productRevision) {
		this.productRevision = productRevision;
	}

	public void setRedundancyGroup(String redundancyGroup) {
		this.redundancyGroup = redundancyGroup;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public void setVendorID(String vendorID) {
		this.vendorID = vendorID;
	}

	public void setVolumeSetSerialNumber(String volumeSetSerialNumber) {
		this.volumeSetSerialNumber = volumeSetSerialNumber;
	}

}
