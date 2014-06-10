package com.afunms.polling.om;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */

public class HostCollectData implements Serializable {

	/** identifier field */
	private Long id;

	/** nullable persistent field */
	private String ipaddress;

	/** nullable persistent field */
	private String restype;

	/** nullable persistent field */
	private String category;

	/** nullable persistent field */
	private String entity;

	/** nullable persistent field */
	private String subentity;

	/** nullable persistent field */
	private String thevalue;

	/** nullable persistent field */
	private java.util.Calendar collecttime;

	/** nullable persistent field */
	private String unit;

	/** nullable persistent field */
	private Long count;

	private String chname;

	private String bak;

	/** default constructor */
	public HostCollectData() {
	}

	/** full constructor */
	public HostCollectData(java.lang.String ipaddress, java.lang.String restype, java.lang.String category, java.lang.String entity, java.lang.String thevalue,
			java.util.Calendar collecttime, java.lang.String unit, Long count) {
		this.ipaddress = ipaddress;
		this.restype = restype;
		this.category = category;
		this.entity = entity;
		this.thevalue = thevalue;
		this.collecttime = collecttime;
		this.unit = unit;
		this.count = count;
	}

	/**
	 * @return
	 */
	public String getBak() {
		return bak;
	}

	public java.lang.String getCategory() {
		return this.category;
	}

	/**
	 * @return
	 */
	public String getChname() {
		return chname;
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
	public Long getCount() {
		return count;
	}

	public java.lang.String getEntity() {
		return this.entity;
	}

	/**
	 * @return
	 */
	public Long getId() {
		return id;
	}

	public java.lang.String getIpaddress() {
		return this.ipaddress;
	}

	public java.lang.String getRestype() {
		return this.restype;
	}

	/**
	 * @return
	 */
	public String getSubentity() {
		return subentity;
	}

	public java.lang.String getThevalue() {
		return this.thevalue;
	}

	public java.lang.String getUnit() {
		return this.unit;
	}

	/**
	 * @param string
	 */
	public void setBak(String string) {
		bak = string;
	}

	public void setCategory(java.lang.String category) {
		this.category = category;
	}

	/**
	 * @param string
	 */
	public void setChname(String string) {
		chname = string;
	}

	/**
	 * @param calendar
	 */
	public void setCollecttime(java.util.Calendar calendar) {
		collecttime = calendar;
	}

	/**
	 * @param long1
	 */
	public void setCount(Long long1) {
		count = long1;
	}

	public void setEntity(java.lang.String entity) {
		this.entity = entity;
	}

	/**
	 * @param integer
	 */
	public void setId(Long l) {
		id = l;
	}

	public void setIpaddress(java.lang.String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setRestype(java.lang.String restype) {
		this.restype = restype;
	}

	/**
	 * @param string
	 */
	public void setSubentity(String string) {
		subentity = string;
	}

	public void setThevalue(java.lang.String thevalue) {
		this.thevalue = thevalue;
	}

	public void setUnit(java.lang.String unit) {
		this.unit = unit;
	}

}
