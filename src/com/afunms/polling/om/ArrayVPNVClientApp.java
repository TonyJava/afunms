package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNVClientApp implements Serializable {

	private int id;

	private String ipaddress;

	private String type;

	private String subtype;

	private Calendar Collecttime;

	private int vclientAppIndex;

	private String vclientAppVirtualSite;

	private long vclientAppBytesIn;

	private long vclientAppBytesOut;

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

	public long getVclientAppBytesIn() {
		return vclientAppBytesIn;
	}

	public long getVclientAppBytesOut() {
		return vclientAppBytesOut;
	}

	public int getVclientAppIndex() {
		return vclientAppIndex;
	}

	public String getVclientAppVirtualSite() {
		return vclientAppVirtualSite;
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

	public void setVclientAppBytesIn(long vclientAppBytesIn) {
		this.vclientAppBytesIn = vclientAppBytesIn;
	}

	public void setVclientAppBytesOut(long vclientAppBytesOut) {
		this.vclientAppBytesOut = vclientAppBytesOut;
	}

	public void setVclientAppIndex(int vclientAppIndex) {
		this.vclientAppIndex = vclientAppIndex;
	}

	public void setVclientAppVirtualSite(String vclientAppVirtualSite) {
		this.vclientAppVirtualSite = vclientAppVirtualSite;
	}

}
