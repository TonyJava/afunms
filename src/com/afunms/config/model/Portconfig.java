package com.afunms.config.model;

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

	private String inportalarm;

	private String outportalarm;
	private String speed;
	private String alarmlevel;
	private String flag;

	//
	// /** full constructor */
	// public Portconfig(Integer id,String ipaddress,Integer portindex, String
	// name, String linkuse, Integer sms, String bak,Integer reportflag,String
	// inportalarm,String outportalarm) {
	// this.id = id;
	// this.ipaddress = ipaddress;
	// this.portindex = portindex;
	// this.name = name;
	// this.linkuse = linkuse;
	// //this.room = roomid;
	// this.sms = sms;
	// this.bak=bak;
	// this.reportflag = reportflag;
	// this.inportalarm = inportalarm;
	// this.outportalarm = outportalarm;
	// }

	/** default constructor */
	public Portconfig() {
	}

	/** full constructor */
	public Portconfig(Integer id, String ipaddress, Integer portindex, String name, String linkuse, Integer sms, String bak, Integer reportflag, String inportalarm,
			String outportalarm, String alarmlevel) {
		this.id = id;
		this.ipaddress = ipaddress;
		this.portindex = portindex;
		this.name = name;
		this.linkuse = linkuse;
		// this.room = roomid;
		this.sms = sms;
		this.bak = bak;
		this.reportflag = reportflag;
		this.inportalarm = inportalarm;
		this.outportalarm = outportalarm;
		this.alarmlevel = alarmlevel;
	}

	public Portconfig(Integer id, String ipaddress, Integer portindex, String name, String linkuse, Integer sms, String bak, Integer reportflag, String inportalarm,
			String outportalarm, String speed, String alarmlevel) {
		this.id = id;
		this.ipaddress = ipaddress;
		this.portindex = portindex;
		this.name = name;
		this.linkuse = linkuse;
		// this.room = roomid;
		this.sms = sms;
		this.bak = bak;
		this.reportflag = reportflag;
		this.inportalarm = inportalarm;
		this.outportalarm = outportalarm;
		this.speed = speed;
		this.alarmlevel = alarmlevel;
	}

	public String getAlarmlevel() {
		return alarmlevel;
	}

	/**
	 * @return
	 */
	public String getBak() {
		return bak;
	}

	public String getFlag() {
		return flag;
	}

	/**
	 * @return
	 */
	public Integer getId() {
		return id;
	}

	public String getInportalarm() {
		return inportalarm;
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

	public String getOutportalarm() {
		return outportalarm;
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

	public String getSpeed() {
		return speed;
	}

	public void setAlarmlevel(String alarmlevel) {
		this.alarmlevel = alarmlevel;
	}

	/**
	 * @param serializable
	 */
	public void setBak(String serializable) {
		bak = serializable;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	/**
	 * @param string
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	public void setInportalarm(String inportalarm) {
		this.inportalarm = inportalarm;
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

	public void setOutportalarm(String outportalarm) {
		this.outportalarm = outportalarm;
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

	public void setSpeed(String speed) {
		this.speed = speed;
	}

}
