package com.afunms.polling.om;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/** @author Hibernate CodeGenerator */
public class CDP extends BaseVo implements Serializable {

	/** identifier field */
	private Long id;

	/** nullable persistent field */
	private Long nodeid;

	/** nullable persistent field */
	private String deviceIP;

	/** nullable persistent field */
	private String portName;

	/** nullable persistent field */
	private java.util.Calendar collecttime;

	/** default constructor */
	public CDP() {
	}

	/**
	 * @return
	 */
	public java.util.Calendar getCollecttime() {
		return collecttime;
	}

	public String getDeviceIP() {
		return deviceIP;
	}

	/**
	 * @return
	 */
	public Long getId() {
		return id;
	}

	public Long getNodeid() {
		return nodeid;
	}

	public String getPortName() {
		return portName;
	}

	/**
	 * @param calendar
	 */
	public void setCollecttime(java.util.Calendar calendar) {
		collecttime = calendar;
	}

	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}

	/**
	 * @param integer
	 */
	public void setId(Long l) {
		id = l;
	}

	public void setNodeid(Long nodeid) {
		this.nodeid = nodeid;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

}
