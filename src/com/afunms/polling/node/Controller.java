package com.afunms.polling.node;

import java.util.List;

public class Controller {

	private String name;
	private String status;
	private String serialNumber;
	private String vendorID;
	private String productID;
	private String productRevision;
	private String firmwareRevision;
	private String manufacturingProductCode;
	private String controllerType;
	private String batteryChargerFirmwareRevision;
	List<CtrlPort> frontPortList;
	List<CtrlPort> backPortList;
	private Battery battery;
	private Processor processor;
	List<DIMM> dimmList;

	private String enclosureSwitchSetting;
	private String driveAddressBasis;
	private String enclosureID;
	private String loopPair;
	private String loopID;
	private String hardAddress;

	public List<CtrlPort> getBackPortList() {
		return backPortList;
	}

	public Battery getBattery() {
		return battery;
	}

	public String getBatteryChargerFirmwareRevision() {
		return batteryChargerFirmwareRevision;
	}

	public String getControllerType() {
		return controllerType;
	}

	public List<DIMM> getDimmList() {
		return dimmList;
	}

	public String getDriveAddressBasis() {
		return driveAddressBasis;
	}

	public String getEnclosureID() {
		return enclosureID;
	}

	public String getEnclosureSwitchSetting() {
		return enclosureSwitchSetting;
	}

	public String getFirmwareRevision() {
		return firmwareRevision;
	}

	public List<CtrlPort> getFrontPortList() {
		return frontPortList;
	}

	public String getHardAddress() {
		return hardAddress;
	}

	public String getLoopID() {
		return loopID;
	}

	public String getLoopPair() {
		return loopPair;
	}

	public String getManufacturingProductCode() {
		return manufacturingProductCode;
	}

	public String getName() {
		return name;
	}

	public Processor getProcessor() {
		return processor;
	}

	public String getProductID() {
		return productID;
	}

	public String getProductRevision() {
		return productRevision;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getStatus() {
		return status;
	}

	public String getVendorID() {
		return vendorID;
	}

	public void setBackPortList(List<CtrlPort> backPortList) {
		this.backPortList = backPortList;
	}

	public void setBattery(Battery battery) {
		this.battery = battery;
	}

	public void setBatteryChargerFirmwareRevision(String batteryChargerFirmwareRevision) {
		this.batteryChargerFirmwareRevision = batteryChargerFirmwareRevision;
	}

	public void setControllerType(String controllerType) {
		this.controllerType = controllerType;
	}

	public void setDimmList(List<DIMM> dimmList) {
		this.dimmList = dimmList;
	}

	public void setDriveAddressBasis(String driveAddressBasis) {
		this.driveAddressBasis = driveAddressBasis;
	}

	public void setEnclosureID(String enclosureID) {
		this.enclosureID = enclosureID;
	}

	public void setEnclosureSwitchSetting(String enclosureSwitchSetting) {
		this.enclosureSwitchSetting = enclosureSwitchSetting;
	}

	public void setFirmwareRevision(String firmwareRevision) {
		this.firmwareRevision = firmwareRevision;
	}

	public void setFrontPortList(List<CtrlPort> frontPortList) {
		this.frontPortList = frontPortList;
	}

	public void setHardAddress(String hardAddress) {
		this.hardAddress = hardAddress;
	}

	public void setLoopID(String loopID) {
		this.loopID = loopID;
	}

	public void setLoopPair(String loopPair) {
		this.loopPair = loopPair;
	}

	public void setManufacturingProductCode(String manufacturingProductCode) {
		this.manufacturingProductCode = manufacturingProductCode;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public void setProductRevision(String productRevision) {
		this.productRevision = productRevision;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setVendorID(String vendorID) {
		this.vendorID = vendorID;
	}
}