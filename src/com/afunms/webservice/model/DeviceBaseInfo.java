package com.afunms.webservice.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @description TODO
 * @author wangxiangyong
 * @date Jan 14, 2012 3:45:51 PM
 */
@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceBaseInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8629380416100811617L;
	private String id;       //ID
	private String ipAddress;//IP地址
	private String sysName;  //系统名称
	private String alias;    //设备名称
	private String netMask;  //子网掩码
	private String sysDescr; //系统描述
	private String sysOid;   //系统OID
	private String category; //类别
	private String osType;   //操作系统类型
	private String status;   //状态
   
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getNetMask() {
		return netMask;
	}

	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}

	public String getSysDescr() {
		return sysDescr;
	}

	public void setSysDescr(String sysDescr) {
		this.sysDescr = sysDescr;
	}

	public String getSysOid() {
		return sysOid;
	}

	public void setSysOid(String sysOid) {
		this.sysOid = sysOid;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
