package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppDumpList extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private Calendar collectTime;

	private String dmpIndex;

	private String dmpStPath;

	private String dmpStAttempts;

	private String dmpStSuccesses;

	private String dmpStFailures;

	private String dmpTime;

	private String dmpStatus;

	private String dmpLevel;

	private String dmpNumFiles;

	private String dmpDataAmount;

	private String dmpStartTime;

	private String dmpDuration;

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getDmpDataAmount() {
		return dmpDataAmount;
	}

	public String getDmpDuration() {
		return dmpDuration;
	}

	public String getDmpIndex() {
		return dmpIndex;
	}

	public String getDmpLevel() {
		return dmpLevel;
	}

	public String getDmpNumFiles() {
		return dmpNumFiles;
	}

	public String getDmpStartTime() {
		return dmpStartTime;
	}

	public String getDmpStAttempts() {
		return dmpStAttempts;
	}

	public String getDmpStatus() {
		return dmpStatus;
	}

	public String getDmpStFailures() {
		return dmpStFailures;
	}

	public String getDmpStPath() {
		return dmpStPath;
	}

	public String getDmpStSuccesses() {
		return dmpStSuccesses;
	}

	public String getDmpTime() {
		return dmpTime;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setDmpDataAmount(String dmpDataAmount) {
		this.dmpDataAmount = dmpDataAmount;
	}

	public void setDmpDuration(String dmpDuration) {
		this.dmpDuration = dmpDuration;
	}

	public void setDmpIndex(String dmpIndex) {
		this.dmpIndex = dmpIndex;
	}

	public void setDmpLevel(String dmpLevel) {
		this.dmpLevel = dmpLevel;
	}

	public void setDmpNumFiles(String dmpNumFiles) {
		this.dmpNumFiles = dmpNumFiles;
	}

	public void setDmpStartTime(String dmpStartTime) {
		this.dmpStartTime = dmpStartTime;
	}

	public void setDmpStAttempts(String dmpStAttempts) {
		this.dmpStAttempts = dmpStAttempts;
	}

	public void setDmpStatus(String dmpStatus) {
		this.dmpStatus = dmpStatus;
	}

	public void setDmpStFailures(String dmpStFailures) {
		this.dmpStFailures = dmpStFailures;
	}

	public void setDmpStPath(String dmpStPath) {
		this.dmpStPath = dmpStPath;
	}

	public void setDmpStSuccesses(String dmpStSuccesses) {
		this.dmpStSuccesses = dmpStSuccesses;
	}

	public void setDmpTime(String dmpTime) {
		this.dmpTime = dmpTime;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

}
