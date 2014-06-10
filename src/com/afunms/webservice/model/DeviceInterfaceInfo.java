package com.afunms.webservice.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @description 设备接口的基本信息
 * @author wangxiangyong
 * @date Jan 14, 2012 3:44:46 PM
 */
@XmlRootElement(name = "interface")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceInterfaceInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4326325399424449029L;
//	private String id; // ID
//	private String ipAddress; // 设备的IP的地址
	private String ifName; // 端口名称
	private String ifType; // 端口类型
	private String ifPhysAddress;// 端口Mac地址

//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}
//
//	public String getIpAddress() {
//		return ipAddress;
//	}
//
//	public void setIpAddress(String ipAddress) {
//		this.ipAddress = ipAddress;
//	}

	public String getIfName() {
		return ifName;
	}

	public void setIfName(String ifName) {
		this.ifName = ifName;
	}

	public String getIfType() {
		return ifType;
	}

	public void setIfType(String ifType) {
		this.ifType = ifType;
	}

	public String getIfPhysAddress() {
		return ifPhysAddress;
	}

	public void setIfPhysAddress(String ifPhysAddress) {
		this.ifPhysAddress = ifPhysAddress;
	}

}
