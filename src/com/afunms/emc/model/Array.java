package com.afunms.emc.model;

import com.afunms.common.base.BaseVo;

public class Array extends BaseVo {
	private String status;
	private String presentWatts;
	private String averagewatts;

	public String getAveragewatts() {
		return averagewatts;
	}

	public String getPresentWatts() {
		return presentWatts;
	}

	public String getStatus() {
		return status;
	}

	public void setAveragewatts(String averagewatts) {
		this.averagewatts = averagewatts;
	}

	public void setPresentWatts(String presentWatts) {
		this.presentWatts = presentWatts;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
