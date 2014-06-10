package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNSSLSysInfor implements Serializable {

	private int id;

	private String ipaddress;

	private String type;

	private String subtype;

	private Calendar Collecttime;

	private String sslStatus;

	private int vhostNum;

	private long totalOpenSSLConns;

	private long totalAcceptedConns;

	private long totalRequestedConns;

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getSslStatus() {
		return sslStatus;
	}

	public String getSubtype() {
		return subtype;
	}

	public long getTotalAcceptedConns() {
		return totalAcceptedConns;
	}

	public long getTotalOpenSSLConns() {
		return totalOpenSSLConns;
	}

	public long getTotalRequestedConns() {
		return totalRequestedConns;
	}

	public String getType() {
		return type;
	}

	public int getVhostNum() {
		return vhostNum;
	}

	public void setCollecttime(Calendar collecttime) {
		Collecttime = collecttime;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setSslStatus(String sslStatus) {
		this.sslStatus = sslStatus;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setTotalAcceptedConns(long totalAcceptedConns) {
		this.totalAcceptedConns = totalAcceptedConns;
	}

	public void setTotalOpenSSLConns(long totalOpenSSLConns) {
		this.totalOpenSSLConns = totalOpenSSLConns;
	}

	public void setTotalRequestedConns(long totalRequestedConns) {
		this.totalRequestedConns = totalRequestedConns;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setVhostNum(int vhostNum) {
		this.vhostNum = vhostNum;
	}
}
