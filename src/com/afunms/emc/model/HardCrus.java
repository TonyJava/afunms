package com.afunms.emc.model;

import java.util.Map;

public class HardCrus {
	private String name;
	private Map<String, String> spState;
	private Map<String, String> powerState;
	private Map<String, String> spsState;
	private Map<String, String> lccState;
	private Map<String, String> spsCablingState;

	public Map<String, String> getLccState() {
		return lccState;
	}

	public String getName() {
		return name;
	}

	public Map<String, String> getPowerState() {
		return powerState;
	}

	public Map<String, String> getSpsCablingState() {
		return spsCablingState;
	}

	public Map<String, String> getSpsState() {
		return spsState;
	}

	public Map<String, String> getSpState() {
		return spState;
	}

	public void setLccState(Map<String, String> lccState) {
		this.lccState = lccState;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPowerState(Map<String, String> powerState) {
		this.powerState = powerState;
	}

	public void setSpsCablingState(Map<String, String> spsCablingState) {
		this.spsCablingState = spsCablingState;
	}

	public void setSpsState(Map<String, String> spsState) {
		this.spsState = spsState;
	}

	public void setSpState(Map<String, String> spState) {
		this.spState = spState;
	}

}
