package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNInterface implements Serializable {

	private int id;

	private String ipaddress;

	private String type;

	private String subtype;

	private Calendar Collecttime;

	private int infIndex;

	private String infDescr;

	private String infOperStatus;

	private String infAddress;

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public int getId() {
		return id;
	}

	public String getInfAddress() {
		return infAddress;
	}

	public String getInfDescr() {
		return infDescr;
	}

	public int getInfIndex() {
		return infIndex;
	}

	public String getInfOperStatus() {
		return infOperStatus;
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

	public void setCollecttime(Calendar collecttime) {
		Collecttime = collecttime;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setInfAddress(String infAddress) {
		this.infAddress = infAddress;
	}

	public void setInfDescr(String infDescr) {
		this.infDescr = infDescr;
	}

	public void setInfIndex(int infIndex) {
		this.infIndex = infIndex;
	}

	public void setInfOperStatus(String infOperStatus) {
		this.infOperStatus = infOperStatus;
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

}
