package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNWeb implements Serializable {

	private int id;

	private String ipaddress;

	private String type;

	private String subtype;

	private Calendar Collecttime;

	private String webId;

	private int webAuthorizedReq;

	private int webUnauthorizedReq;

	private long webBytesIn;

	private long webBytesOut;

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getType() {
		return type;
	}

	public int getWebAuthorizedReq() {
		return webAuthorizedReq;
	}

	public long getWebBytesIn() {
		return webBytesIn;
	}

	public long getWebBytesOut() {
		return webBytesOut;
	}

	public String getWebId() {
		return webId;
	}

	public int getWebUnauthorizedReq() {
		return webUnauthorizedReq;
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

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setWebAuthorizedReq(int webAuthorizedReq) {
		this.webAuthorizedReq = webAuthorizedReq;
	}

	public void setWebBytesIn(long webBytesIn) {
		this.webBytesIn = webBytesIn;
	}

	public void setWebBytesOut(long webBytesOut) {
		this.webBytesOut = webBytesOut;
	}

	public void setWebId(String webId) {
		this.webId = webId;
	}

	public void setWebUnauthorizedReq(int webUnauthorizedReq) {
		this.webUnauthorizedReq = webUnauthorizedReq;
	}

}
