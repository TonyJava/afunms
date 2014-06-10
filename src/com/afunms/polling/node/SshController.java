package com.afunms.polling.node;

/**
 * 
 * @descrition TODO
 * @author wangxiangyong
 * @date Jun 15, 2013 6:06:04 PM
 */
public class SshController {
	private String id;
	private String SerialNum;
	private String hardwareVersion;
	private String cpldVersion;
	private String mac;
	private String wwnn;
	private String ip;
	private String mask;
	private String gateway;
	private String disks;
	private String vdisks;
	private String cache;
	private String hostPorts;
	private String diskChannels;
	private String diskBusType;
	private String status;
	private String failedOver;
	private String failOverReason;

	public String getCache() {
		return cache;
	}

	public String getCpldVersion() {
		return cpldVersion;
	}

	public String getDiskBusType() {
		return diskBusType;
	}

	public String getDiskChannels() {
		return diskChannels;
	}

	public String getDisks() {
		return disks;
	}

	public String getFailedOver() {
		return failedOver;
	}

	public String getFailOverReason() {
		return failOverReason;
	}

	public String getGateway() {
		return gateway;
	}

	public String getHardwareVersion() {
		return hardwareVersion;
	}

	public String getHostPorts() {
		return hostPorts;
	}

	public String getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public String getMac() {
		return mac;
	}

	public String getMask() {
		return mask;
	}

	public String getSerialNum() {
		return SerialNum;
	}

	public String getStatus() {
		return status;
	}

	public String getVdisks() {
		return vdisks;
	}

	public String getWwnn() {
		return wwnn;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public void setCpldVersion(String cpldVersion) {
		this.cpldVersion = cpldVersion;
	}

	public void setDiskBusType(String diskBusType) {
		this.diskBusType = diskBusType;
	}

	public void setDiskChannels(String diskChannels) {
		this.diskChannels = diskChannels;
	}

	public void setDisks(String disks) {
		this.disks = disks;
	}

	public void setFailedOver(String failedOver) {
		this.failedOver = failedOver;
	}

	public void setFailOverReason(String failOverReason) {
		this.failOverReason = failOverReason;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public void setHardwareVersion(String hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}

	public void setHostPorts(String hostPorts) {
		this.hostPorts = hostPorts;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public void setSerialNum(String serialNum) {
		SerialNum = serialNum;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setVdisks(String vdisks) {
		this.vdisks = vdisks;
	}

	public void setWwnn(String wwnn) {
		this.wwnn = wwnn;
	}

}
