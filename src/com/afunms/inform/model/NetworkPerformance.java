package com.afunms.inform.model;

public class NetworkPerformance {
	private int nodeId;
	private String ipAddress;
	private String alias;
	private float cpuValue;
	private float memValue;
	private float ifUtil;

	public String getAlias() {
		return alias;
	}

	public float getCpuValue() {
		return cpuValue;
	}

	/**
	 * @return the ifUtil
	 */
	public float getIfUtil() {
		return ifUtil;
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

	/**
	 * @param ifUtil
	 *            the ifUtil to set
	 */
	public void setIfUtil(float ifUtil) {
		this.ifUtil = ifUtil;
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
