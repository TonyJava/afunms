package com.afunms.inform.model;

import com.afunms.common.base.BaseVo;

public class ServerPerformance extends BaseVo {
	private int nodeId;
	private String ipAddress;
	private String alias;
	private float cpuValue;
	private float memValue;
	private float diskValue;

	public String getAlias() {
		return alias;
	}

	public float getCpuValue() {
		return cpuValue;
	}

	public float getDiskValue() {
		return diskValue;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public float getMemValue() {
		return memValue;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setCpuValue(float cpuValue) {
		this.cpuValue = cpuValue;
	}

	public void setDiskValue(float diskValue) {
		this.diskValue = diskValue;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setMemValue(float memValue) {
		this.memValue = memValue;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
}
