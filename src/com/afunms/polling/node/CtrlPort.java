package com.afunms.polling.node;

public class CtrlPort {

	private String name;
	private String status;
	private String portInstance;
	private String hardAddress;
	private String linkState;
	private String nodeWWN;
	private String portWWN;
	private String topology;
	private String dataRate;
	private String portID;
	private String deviceHostName;
	private String hardwarePath;
	private String devicePath;

	public String getDataRate() {
		return dataRate;
	}

	public String getDeviceHostName() {
		return deviceHostName;
	}

	public String getDevicePath() {
		return devicePath;
	}

	public String getHardAddress() {
		return hardAddress;
	}

	public String getHardwarePath() {
		return hardwarePath;
	}

	public String getLinkState() {
		return linkState;
	}

	public String getName() {
		return name;
	}

	public String getNodeWWN() {
		return nodeWWN;
	}

	public String getPortID() {
		return portID;
	}

	public String getPortInstance() {
		return portInstance;
	}

	public String getPortWWN() {
		return portWWN;
	}

	public String getStatus() {
		return status;
	}

	public String getTopology() {
		return topology;
	}

	public void setDataRate(String dataRate) {
		this.dataRate = dataRate;
	}

	public void setDeviceHostName(String deviceHostName) {
		this.deviceHostName = deviceHostName;
	}

	public void setDevicePath(String devicePath) {
		this.devicePath = devicePath;
	}

	public void setHardAddress(String hardAddress) {
		this.hardAddress = hardAddress;
	}

	public void setHardwarePath(String hardwarePath) {
		this.hardwarePath = hardwarePath;
	}

	public void setLinkState(String linkState) {
		this.linkState = linkState;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNodeWWN(String nodeWWN) {
		this.nodeWWN = nodeWWN;
	}

	public void setPortID(String portID) {
		this.portID = portID;
	}

	public void setPortInstance(String portInstance) {
		this.portInstance = portInstance;
	}

	public void setPortWWN(String portWWN) {
		this.portWWN = portWWN;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTopology(String topology) {
		this.topology = topology;
	}

}
