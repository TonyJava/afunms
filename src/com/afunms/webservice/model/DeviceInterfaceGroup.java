package com.afunms.webservice.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @description 设备接口组的基本信息
 * @author wangxiangyong
 * @date Jan 14, 2012 3:44:46 PM
 */
@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceInterfaceGroup implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6334023997621192374L;
	// private String id; // ID
	private String ipAddress;// 设备的IP地址
	private List<DeviceInterfaceInfo> group;

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public List<DeviceInterfaceInfo> getGroup() {
		return group;
	}

	public void setGroup(List<DeviceInterfaceInfo> group) {
		this.group = group;
	}

}
