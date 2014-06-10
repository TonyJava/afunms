package com.afunms.polling.node;

/**
 * 
 * @descrition æÌ–≈œ¢
 * @author wangxiangyong
 * @date Jun 18, 2013 10:34:04 AM
 */
public class Volume {
	private String vdiskName;
	private String size;
	private String serialNumber;
	private String wrPolicy;
	private String cacheOpt;
	private String readAheadSize;
	private String type;
	private String classes;
	private String description;

	public String getCacheOpt() {
		return cacheOpt;
	}

	public String getClasses() {
		return classes;
	}

	public String getDescription() {
		return description;
	}

	public String getReadAheadSize() {
		return readAheadSize;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getSize() {
		return size;
	}

	public String getType() {
		return type;
	}

	public String getVdiskName() {
		return vdiskName;
	}

	public String getWrPolicy() {
		return wrPolicy;
	}

	public void setCacheOpt(String cacheOpt) {
		this.cacheOpt = cacheOpt;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setReadAheadSize(String readAheadSize) {
		this.readAheadSize = readAheadSize;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setVdiskName(String vdiskName) {
		this.vdiskName = vdiskName;
	}

	public void setWrPolicy(String wrPolicy) {
		this.wrPolicy = wrPolicy;
	}

}
