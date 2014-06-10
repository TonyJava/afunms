package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppRestore extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private Calendar collectTime;

	private String rstActives;

	private String rstAttempts;

	private String rstSuccesses;

	private String rstFailures;

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getRstActives() {
		return rstActives;
	}

	public String getRstAttempts() {
		return rstAttempts;
	}

	public String getRstFailures() {
		return rstFailures;
	}

	public String getRstSuccesses() {
		return rstSuccesses;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setRstActives(String rstActives) {
		this.rstActives = rstActives;
	}

	public void setRstAttempts(String rstAttempts) {
		this.rstAttempts = rstAttempts;
	}

	public void setRstFailures(String rstFailures) {
		this.rstFailures = rstFailures;
	}

	public void setRstSuccesses(String rstSuccesses) {
		this.rstSuccesses = rstSuccesses;
	}

}
