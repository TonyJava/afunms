package com.afunms.emc.model;

import com.afunms.common.base.BaseVo;

public class MemModel extends BaseVo {
	private String id;
	private String name;
	private String powerStatus;
	private String presentWatts;
	private String averageWatts;
	private String airStatus;
	private String presentDegree;
	private String averageDegree;

	public String getAirStatus() {
		return airStatus;
	}

	public String getAverageDegree() {
		return averageDegree;
	}

	public String getAverageWatts() {
		return averageWatts;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPowerStatus() {
		return powerStatus;
	}

	public String getPresentDegree() {
		return presentDegree;
	}

	public String getPresentWatts() {
		return presentWatts;
	}

	public void setAirStatus(String airStatus) {
		this.airStatus = airStatus;
	}

	public void setAverageDegree(String averageDegree) {
		this.averageDegree = averageDegree;
	}

	public void setAverageWatts(String averageWatts) {
		this.averageWatts = averageWatts;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPowerStatus(String powerStatus) {
		this.powerStatus = powerStatus;
	}

	public void setPresentDegree(String presentDegree) {
		this.presentDegree = presentDegree;
	}

	public void setPresentWatts(String presentWatts) {
		this.presentWatts = presentWatts;
	}

}
