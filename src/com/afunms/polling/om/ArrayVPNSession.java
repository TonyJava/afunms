package com.afunms.polling.om;

import java.io.Serializable;

public class ArrayVPNSession implements Serializable {

	private int id;

	private String ipaddress;

	private String type;

	private String subtype;

	private String Collecttime;

	private int numSessions;

	private int successLogin;

	private int successLogout;

	private int failureLogin;

	private long totalBytesIn;

	private long totalBytesOut;

	private int maxActiveSessions;

	private int errorLogin;

	private int lockOutLogin;

	public String getCollecttime() {
		return Collecttime;
	}

	public int getErrorLogin() {
		return errorLogin;
	}

	public int getFailureLogin() {
		return failureLogin;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public int getLockOutLogin() {
		return lockOutLogin;
	}

	public int getMaxActiveSessions() {
		return maxActiveSessions;
	}

	public int getNumSessions() {
		return numSessions;
	}

	public String getSubtype() {
		return subtype;
	}

	public int getSuccessLogin() {
		return successLogin;
	}

	public int getSuccessLogout() {
		return successLogout;
	}

	public long getTotalBytesIn() {
		return totalBytesIn;
	}

	public long getTotalBytesOut() {
		return totalBytesOut;
	}

	public String getType() {
		return type;
	}

	public void setCollecttime(String collecttime) {
		Collecttime = collecttime;
	}

	public void setErrorLogin(int errorLogin) {
		this.errorLogin = errorLogin;
	}

	public void setFailureLogin(int failureLogin) {
		this.failureLogin = failureLogin;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setLockOutLogin(int lockOutLogin) {
		this.lockOutLogin = lockOutLogin;
	}

	public void setMaxActiveSessions(int maxActiveSessions) {
		this.maxActiveSessions = maxActiveSessions;
	}

	public void setNumSessions(int numSessions) {
		this.numSessions = numSessions;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setSuccessLogin(int successLogin) {
		this.successLogin = successLogin;
	}

	public void setSuccessLogout(int successLogout) {
		this.successLogout = successLogout;
	}

	public void setTotalBytesIn(long totalBytesIn) {
		this.totalBytesIn = totalBytesIn;
	}

	public void setTotalBytesOut(long totalBytesOut) {
		this.totalBytesOut = totalBytesOut;
	}

	public void setType(String type) {
		this.type = type;
	}
}
