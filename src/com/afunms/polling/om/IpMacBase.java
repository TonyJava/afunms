package com.afunms.polling.om;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/** @author Hibernate CodeGenerator */
public class IpMacBase extends BaseVo implements Serializable {

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
	private int ifband;

	/** nullable persistent field */
	private String ifsms;
	private String iftel;
	private String ifemail;
	private int employee_id;

	private String bak;

	/** default constructor */
	public IpMacBase() {
	}

	/** full constructor */
	public IpMacBase(java.lang.String relateipaddr, java.lang.String ifindex, java.lang.String ipaddress, java.lang.String mac, java.util.Calendar collecttime, int ifband,
			java.lang.String ifsms, String iftel, String ifemail, int employee_id) {
		this.relateipaddr = relateipaddr;
		this.ifindex = ifindex;
		this.ipaddress = ipaddress;
		this.mac = mac;
		this.collecttime = collecttime;
		this.ifband = ifband;
		this.ifsms = ifsms;
		this.iftel = iftel;
		this.ifemail = ifemail;
		this.employee_id = employee_id;
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
	public int getEmployee_id() {
		return employee_id;
	}

	/**
	 * @return
	 */
	public Long getId() {
		return id;
	}

	public int getIfband() {
		return this.ifband;
	}

	public java.lang.String getIfemail() {
		return this.ifemail;
	}

	public java.lang.String getIfindex() {
		return this.ifindex;
	}

	public java.lang.String getIfsms() {
		return this.ifsms;
	}

	public java.lang.String getIftel() {
		return this.iftel;
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
	public void setEmployee_id(int employee_id) {
		this.employee_id = employee_id;
	}

	/**
	 * @param integer
	 */
	public void setId(Long l) {
		id = l;
	}

	public void setIfband(int ifband) {
		this.ifband = ifband;
	}

	public void setIfemail(java.lang.String ifemail) {
		this.ifemail = ifemail;
	}

	public void setIfindex(java.lang.String ifindex) {
		this.ifindex = ifindex;
	}

	public void setIfsms(java.lang.String ifsms) {
		this.ifsms = ifsms;
	}

	public void setIftel(java.lang.String iftel) {
		this.iftel = iftel;
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
}
