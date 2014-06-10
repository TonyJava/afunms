package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNFlowRate implements Serializable {

	private int id;

	private String ipaddress;

	private String type;

	private String subtype;

	private Calendar Collecttime;

	private int totalBytesRcvd;

	private int totalBytesSent;

	private int rcvdBytesPerSec;

	private int sentBytesPerSec;

	private int peakRcvdBytesPerSec;

	private int peakSentBytesPerSec;

	private int activeTransac;

	public int getActiveTransac() {
		return activeTransac;
	}

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public int getPeakRcvdBytesPerSec() {
		return peakRcvdBytesPerSec;
	}

	public int getPeakSentBytesPerSec() {
		return peakSentBytesPerSec;
	}

	public int getRcvdBytesPerSec() {
		return rcvdBytesPerSec;
	}

	public int getSentBytesPerSec() {
		return sentBytesPerSec;
	}

	public String getSubtype() {
		return subtype;
	}

	public int getTotalBytesRcvd() {
		return totalBytesRcvd;
	}

	public int getTotalBytesSent() {
		return totalBytesSent;
	}

	public String getType() {
		return type;
	}

	public void setActiveTransac(int activeTransac) {
		this.activeTransac = activeTransac;
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

	public void setPeakRcvdBytesPerSec(int peakRcvdBytesPerSec) {
		this.peakRcvdBytesPerSec = peakRcvdBytesPerSec;
	}

	public void setPeakSentBytesPerSec(int peakSentBytesPerSec) {
		this.peakSentBytesPerSec = peakSentBytesPerSec;
	}

	public void setRcvdBytesPerSec(int rcvdBytesPerSec) {
		this.rcvdBytesPerSec = rcvdBytesPerSec;
	}

	public void setSentBytesPerSec(int sentBytesPerSec) {
		this.sentBytesPerSec = sentBytesPerSec;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setTotalBytesRcvd(int totalBytesRcvd) {
		this.totalBytesRcvd = totalBytesRcvd;
	}

	public void setTotalBytesSent(int totalBytesSent) {
		this.totalBytesSent = totalBytesSent;
	}

	public void setType(String type) {
		this.type = type;
	}

}
