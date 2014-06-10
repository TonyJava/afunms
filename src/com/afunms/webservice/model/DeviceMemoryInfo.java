package com.afunms.webservice.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @description 内存信息
 * @author wangxiangyong
 * @date Jan 14, 2012 3:44:46 PM
 */
@XmlRootElement(name = "memory")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceMemoryInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5877675331698817062L;
	private String memoryName;// 内存名称
	private String allSize; // 总容量
	//private String type; // 类型
	//private String unit; // 单位

	public String getMemoryName() {
		return memoryName;
	}

	public void setMemoryName(String memoryName) {
		this.memoryName = memoryName;
	}

	public String getAllSize() {
		return allSize;
	}

	public void setAllSize(String allSize) {
		this.allSize = allSize;
	}

//	public String getType() {
//		return type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}
//
//	public String getUnit() {
//		return unit;
//	}
//
//	public void setUnit(String unit) {
//		this.unit = unit;
//	}

}
