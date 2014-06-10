package com.afunms.webservice.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @description CPU信息
 * @author wangxiangyong
 * @date Jan 14, 2012 3:44:46 PM
 */
@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceCpuInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 177252331086240152L;
	private int cpuCount;// cpu个数
	private String cmpName;// 生产厂家

	public int getCpuCount() {
		return cpuCount;
	}

	public void setCpuCount(int cpuCount) {
		this.cpuCount = cpuCount;
	}

	public String getCmpName() {
		return cmpName;
	}

	public void setCmpName(String cmpName) {
		this.cmpName = cmpName;
	}

}
