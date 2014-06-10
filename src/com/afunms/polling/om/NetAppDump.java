package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppDump extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private Calendar collectTime;

	private String dmpActives;

	private String dmpAttempts;

	private String dmpSuccesses;

	private String dmpFailures;

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getDmpActives() {
		return dmpActives;
	}

	public String getDmpAttempts() {
		return dmpAttempts;
	}

	public String getDmpFailures() {
		return dmpFailures;
	}

	public String getDmpSuccesses() {
		return dmpSuccesses;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setDmpActives(String dmpActives) {
		this.dmpActives = dmpActives;
	}

	public void setDmpAttempts(String dmpAttempts) {
		this.dmpAttempts = dmpAttempts;
	}

	public void setDmpFailures(String dmpFailures) {
		this.dmpFailures = dmpFailures;
	}

	public void setDmpSuccesses(String dmpSuccesses) {
		this.dmpSuccesses = dmpSuccesses;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

}
