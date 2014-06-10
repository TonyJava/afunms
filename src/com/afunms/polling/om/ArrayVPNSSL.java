package com.afunms.polling.om;

/**
 * author ChengFeng
 */
import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNSSL implements Serializable {

	private int Id;

	private String ipaddress;

	private String type;

	private String subType;

	private int sslStatus;

	private int vhostNum;

	private int totalOpenSSLConns;

	private int totalAcceptedConns;

	private int totalRequestedConns;

	private int sslIndex;

	private String vhostName;

	private int openSSLConns;

	private int acceptedConns;

	private int requestedConns;

	private int resumedSess;

	private int resumableSess;

	private int missSess;

	private Calendar Collecttime;

	public int getAcceptedConns() {
		return acceptedConns;
	}

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public int getId() {
		return Id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public int getMissSess() {
		return missSess;
	}

	public int getOpenSSLConns() {
		return openSSLConns;
	}

	public int getRequestedConns() {
		return requestedConns;
	}

	public int getResumableSess() {
		return resumableSess;
	}

	public int getResumedSess() {
		return resumedSess;
	}

	public int getSslIndex() {
		return sslIndex;
	}

	public int getSslStatus() {
		return sslStatus;
	}

	public String getSubType() {
		return subType;
	}

	public int getTotalAcceptedConns() {
		return totalAcceptedConns;
	}

	public int getTotalOpenSSLConns() {
		return totalOpenSSLConns;
	}

	public int getTotalRequestedConns() {
		return totalRequestedConns;
	}

	public String getType() {
		return type;
	}

	public String getVhostName() {
		return vhostName;
	}

	public int getVhostNum() {
		return vhostNum;
	}

	public void setAcceptedConns(int acceptedConns) {
		this.acceptedConns = acceptedConns;
	}

	public void setCollecttime(Calendar collecttime) {
		Collecttime = collecttime;
	}

	public void setId(int Id) {
		this.Id = Id;
	}

	public void setIpaddress(String ip) {
		this.ipaddress = ip;
	}

	public void setMissSess(int missSess) {
		this.missSess = missSess;
	}

	public void setOpenSSLConns(int openSSLConns) {
		this.openSSLConns = openSSLConns;
	}

	public void setRequestedConns(int requestedConns) {
		this.requestedConns = requestedConns;
	}

	public void setResumableSess(int resumableSess) {
		this.resumableSess = resumableSess;
	}

	public void setResumedSess(int resumedSess) {
		this.resumedSess = resumedSess;
	}

	public void setSslIndex(int sslIndex) {
		this.sslIndex = sslIndex;
	}

	public void setSslStatus(int sslStatus) {
		this.sslStatus = sslStatus;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public void setTotalAcceptedConns(int totalAcceptedConns) {
		this.totalAcceptedConns = totalAcceptedConns;
	}

	public void setTotalOpenSSLConns(int totalOpenSSLConns) {
		this.totalOpenSSLConns = totalOpenSSLConns;
	}

	public void setTotalRequestedConns(int totalRequestedConns) {
		this.totalRequestedConns = totalRequestedConns;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setVhostName(String vhostName) {
		this.vhostName = vhostName;
	}

	public void setVhostNum(int vhostNum) {
		this.vhostNum = vhostNum;
	}

}
