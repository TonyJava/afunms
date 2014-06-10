package com.afunms.config.model;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/** @author Hibernate CodeGenerator */
public class NetNodeCfgFile extends BaseVo implements Serializable {

	/** identifier field */
	private Long id;

	/** nullable persistent field */
	private String ipaddress;

	/** nullable persistent field */
	private String name;

	/** nullable persistent field */
	private java.util.Calendar recordtime;

	/** default constructor */
	public NetNodeCfgFile() {
	}

	/** full constructor */
	public NetNodeCfgFile(java.lang.String ipaddress, java.lang.String name, java.util.Calendar recordtime) {
		this.ipaddress = ipaddress;
		this.name = name;
		this.recordtime = recordtime;
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

	public java.lang.String getName() {
		return this.name;
	}

	/**
	 * @return
	 */
	public java.util.Calendar getRecordtime() {
		return recordtime;
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

	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * @param calendar
	 */
	public void setRecordtime(java.util.Calendar recordtime) {
		this.recordtime = recordtime;
	}
}
