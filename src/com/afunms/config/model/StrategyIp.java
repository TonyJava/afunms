package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class StrategyIp extends BaseVo {
	private int id;
	private int StrategyId;
	private String strategyName;
	private String ip;
	private String deviceType;
	private int availability;

	public int getAvailability() {
		return availability;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public int getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public int getStrategyId() {
		return StrategyId;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setAvailability(int availability) {
		this.availability = availability;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setStrategyId(int strategyId) {
		StrategyId = strategyId;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

}
