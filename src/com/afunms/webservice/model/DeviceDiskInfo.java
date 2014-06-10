package com.afunms.webservice.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @description 磁盘信息
 * @author wangxiangyong
 * @date Jan 14, 2012 3:44:46 PM
 */
@XmlRootElement(name = "disk")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceDiskInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -823244132179774082L;
	private String diskName;
	private String allsize;
//	private String unit; // 单位

	public String getDiskName() {
		return diskName;
	}

	public void setDiskName(String diskName) {
		this.diskName = diskName;
	}

	public String getAllsize() {
		return allsize;
	}

	public void setAllsize(String allsize) {
		this.allsize = allsize;
	}

//	public String getUnit() {
//		return unit;
//	}
//
//	public void setUnit(String unit) {
//		this.unit = unit;
//	}

}
