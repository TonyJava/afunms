package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppEnvironment extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private Calendar collectTime;

	private String envOverTemperature;

	private String envFailedFanCount;

	private String envFailedFanMessage;

	private String envFailedPowerSupplyCount;

	private String envFailedPowerSupplyMessage;

	private String nvramBatteryStatus;

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getEnvFailedFanCount() {
		return envFailedFanCount;
	}

	public String getEnvFailedFanMessage() {
		return envFailedFanMessage;
	}

	public String getEnvFailedPowerSupplyCount() {
		return envFailedPowerSupplyCount;
	}

	public String getEnvFailedPowerSupplyMessage() {
		return envFailedPowerSupplyMessage;
	}

	public String getEnvOverTemperature() {
		return envOverTemperature;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getNvramBatteryStatus() {
		return nvramBatteryStatus;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setEnvFailedFanCount(String envFailedFanCount) {
		this.envFailedFanCount = envFailedFanCount;
	}

	public void setEnvFailedFanMessage(String envFailedFanMessage) {
		this.envFailedFanMessage = envFailedFanMessage;
	}

	public void setEnvFailedPowerSupplyCount(String envFailedPowerSupplyCount) {
		this.envFailedPowerSupplyCount = envFailedPowerSupplyCount;
	}

	public void setEnvFailedPowerSupplyMessage(String envFailedPowerSupplyMessage) {
		this.envFailedPowerSupplyMessage = envFailedPowerSupplyMessage;
	}

	public void setEnvOverTemperature(String envOverTemperature) {
		this.envOverTemperature = envOverTemperature;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setNvramBatteryStatus(String nvramBatteryStatus) {
		this.nvramBatteryStatus = nvramBatteryStatus;
	}

}
