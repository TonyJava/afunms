package com.afunms.polling.om;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/** @author Hibernate CodeGenerator */
public class MacHistory extends BaseVo implements Serializable {

	/** identifier field */
	private Long id;

	/** nullable persistent field */
	private String relateipaddr;

	/** nullable persistent field */
	private String ifindex;

	/** nullable persistent field */
	private String ipaddress;

	/** nullable persistent field */
	private String mac;

	/** nullable persistent field */
	private java.util.Calendar collecttime;

	/** nullable persistent field */
	private String thevalue;

	private String bak;

	/** default constructor */
	public MacHistory() {
	}

	/** full constructor */
	public MacHistory(java.lang.String relateipaddr, java.lang.String ifindex, java.lang.String ipaddress, java.lang.String mac, java.util.Calendar collecttime,
			java.lang.String thevalue) {
		this.relateipaddr = relateipaddr;
		this.ifindex = ifindex;
		this.ipaddress = ipaddress;
		this.mac = mac;
		this.collecttime = collecttime;
		this.thevalue = thevalue;
	}

	/**
	 * @return
	 */
	public String getBak() {
		return bak;
	}

	/**
	 * @return
	 */
	public java.util.Calendar getCollecttime() {
		return collecttime;
	}

	/**
	 * @return
	 */
	public Long getId() {
		return id;
	}

	public java.lang.String getIfindex() {
		return this.ifindex;
	}

	public java.lang.String getIpaddress() {
		return this.ipaddress;
	}

	public java.lang.String getMac() {
		return this.mac;
	}

	public java.lang.String getRelateipaddr() {
		return this.relateipaddr;
	}

	public java.lang.String getThevalue() {
		return this.thevalue;
	}

	/**
	 * @param string
	 */
	public void setBak(String string) {
		bak = string;
	}

	/**
	 * @param calendar
	 */
	public void setCollecttime(java.util.Calendar calendar) {
		collecttime = calendar;
	}

	/**
	 * @param integer
	 */
	public void setId(Long l) {
		id = l;
	}

	public void setIfindex(java.lang.String ifindex) {
		this.ifindex = ifindex;
	}

	public void setIpaddress(java.lang.String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setMac(java.lang.String mac) {
		this.mac = mac;
	}

	public void setRelateipaddr(java.lang.String relateipaddr) {
		this.relateipaddr = relateipaddr;
	}

	public void setThevalue(java.lang.String thevalue) {
		this.thevalue = thevalue;
	}
}
