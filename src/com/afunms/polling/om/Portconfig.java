package com.afunms.polling.om;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/** @author Hibernate CodeGenerator */
public class Portconfig extends BaseVo implements Serializable {
	private Integer id;
	/** nullable persistent field */
	private String ipaddress;

	/** nullable persistent field */
	private Integer portindex;

	/** identifier field */
	private String name;

	/** nullable persistent field */
	private String linkuse;

	/** nullable persistent field */
	private Integer sms;

	/** nullable persistent field */
	private String bak;

	private Integer reportflag;

	/** default constructor */
	public Portconfig() {
	}

	/** full constructor */
	public Portconfig(Integer id, String ipaddress, Integer portindex, String name, String linkuse, Integer sms, String bak, Integer reportflag) {
		this.id = id;
		this.ipaddress = ipaddress;
		this.portindex = portindex;
		this.name = name;
		this.linkuse = linkuse;
		// this.room = roomid;
		this.sms = sms;
		this.bak = bak;
		this.reportflag = reportflag;
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
	public Integer getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	/**
	 * @return
	 */
	public String getLinkuse() {
		return linkuse;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public Integer getPortindex() {
		return portindex;
	}

	public Integer getReportflag() {
		return reportflag;
	}

	/**
	 * @return
	 */
	public Integer getSms() {
		return sms;
	}

	/**
	 * @param serializable
	 */
	public void setBak(String serializable) {
		bak = serializable;
	}

	/**
	 * @param string
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param string
	 */
	public void setIpaddress(String string) {
		ipaddress = string;
	}

	/**
	 * @param string
	 */
	public void setLinkuse(String string) {
		linkuse = string;
	}

	/**
	 * @param calendar
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param string
	 */
	public void setPortindex(Integer string) {
		portindex = string;
	}

	public void setReportflag(Integer reportflag) {
		this.reportflag = reportflag;
	}

	/**
	 * @param string
	 */
	public void setSms(Integer string) {
		sms = string;
	}

}
