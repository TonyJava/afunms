package com.afunms.polling.om;

import java.io.Serializable;

public class Avgcollectdata implements Serializable {

	/** nullable persistent field */
	private String ipaddress;

	/** nullable persistent field */
	private String thevalue;

	private String bak;

	private String unit;

	public String getBak() {
		return bak;
	}

	public java.lang.String getIpaddress() {
		return this.ipaddress;
	}

	public java.lang.String getThevalue() {
		return this.thevalue;
	}

	public String getUnit() {
		return unit;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public void setIpaddress(java.lang.String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setThevalue(java.lang.String thevalue) {
		this.thevalue = thevalue;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}