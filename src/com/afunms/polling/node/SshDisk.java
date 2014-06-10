package com.afunms.polling.node;

/**
 * 
 * @descrition TODO
 * @author wangxiangyong
 * @date Jun 16, 2013 11:09:45 AM
 */
public class SshDisk {
	private String location;
	private String serialNumber;
	private String vendor;
	private String rev;
	private String howUsed;
	private String type;
	private String size;
	private String rate;
	private String status;

	public String getHowUsed() {
		return howUsed;
	}

	public String getLocation() {
		return location;
	}

	public String getRate() {
		return rate;
	}

	public String getRev() {
		return rev;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getSize() {
		return size;
	}

	public String getStatus() {
		return status;
	}

	public String getType() {
		return type;
	}

	public String getVendor() {
		return vendor;
	}

	public void setHowUsed(String howUsed) {
		this.howUsed = howUsed;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

}
