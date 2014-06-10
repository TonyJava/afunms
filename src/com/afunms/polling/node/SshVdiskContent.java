package com.afunms.polling.node;

/**
 * 
 * @descrition TODO
 * @author wangxiangyong
 * @date Jun 19, 2013 4:26:38 PM
 */
public class SshVdiskContent {
	private String chassis;
	private String vendor;
	private String productID;
	private String cpld;
	private String aBus;
	private String bBus;
	private String wwpn;
	private String status;
	private String health;

	public String getABus() {
		return aBus;
	}

	public String getBBus() {
		return bBus;
	}

	public String getChassis() {
		return chassis;
	}

	public String getCpld() {
		return cpld;
	}

	public String getHealth() {
		return health;
	}

	public String getProductID() {
		return productID;
	}

	public String getStatus() {
		return status;
	}

	public String getVendor() {
		return vendor;
	}

	public String getWwpn() {
		return wwpn;
	}

	public void setABus(String bus) {
		aBus = bus;
	}

	public void setBBus(String bus) {
		bBus = bus;
	}

	public void setChassis(String chassis) {
		this.chassis = chassis;
	}

	public void setCpld(String cpld) {
		this.cpld = cpld;
	}

	public void setHealth(String health) {
		this.health = health;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public void setWwpn(String wwpn) {
		this.wwpn = wwpn;
	}

}
