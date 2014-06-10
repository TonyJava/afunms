package com.afunms.config.model;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/**
 * mq 通道信息
 * 
 * @author
 * 
 */
public class MQchannel extends BaseVo implements Serializable {

	private Integer id;
	/** nullable persistent field */
	private String ipaddress;

	/** nullable persistent field */
	private Integer channelindex;

	/** identifier field */
	private String name;
	/** identifier field */
	private String mqName;

	/** nullable persistent field */
	private String linkuse;

	/** nullable persistent field */
	private Integer sms;

	/** nullable persistent field */
	private String bak;

	private Integer reportflag;
	private int monflag;

	private Integer sms1;
	private Integer sms2;
	private Integer sms3;

	private int limenvalue;
	private int limenvalue1;
	private int limenvalue2;

	/** default constructor */
	public MQchannel() {
	}

	/** full constructor */
	public MQchannel(Integer id, String ipaddress, Integer portindex, String name, String linkuse, Integer sms, String bak, Integer reportflag) {
		this.id = id;
		this.ipaddress = ipaddress;
		this.name = name;
		this.linkuse = linkuse;
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
	public Integer getChannelindexx() {
		return channelindex;
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
	public int getLimenvalue() {
		return limenvalue;
	}

	/**
	 * @param integer
	 */

	public int getLimenvalue1() {
		return limenvalue1;
	}

	public int getLimenvalue2() {
		return limenvalue2;
	}

	/**
	 * @return
	 */
	public String getLinkuse() {
		return linkuse;
	}

	public int getMonflag() {
		return monflag;
	}

	public String getMqName() {
		return mqName;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
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

	public Integer getSms1() {
		return sms1;
	}

	public Integer getSms2() {
		return sms2;
	}

	public Integer getSms3() {
		return sms3;
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
	public void setChannelindex(Integer string) {
		channelindex = string;
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
	 * @param float1
	 */
	public void setLimenvalue(int float1) {
		limenvalue = float1;
	}

	/**
	 * @param float1
	 */
	public void setLimenvalue1(int float1) {
		limenvalue1 = float1;
	}

	/**
	 * @param float1
	 */
	public void setLimenvalue2(int float1) {
		limenvalue2 = float1;
	}

	/**
	 * @param string
	 */
	public void setLinkuse(String string) {
		linkuse = string;
	}

	public void setMonflag(int monflag) {
		this.monflag = monflag;
	}

	public void setMqName(String mqName) {
		this.mqName = mqName;
	}

	/**
	 * @param calendar
	 */
	public void setName(String string) {
		name = string;
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

	public void setSms1(Integer string) {
		sms1 = string;
	}

	public void setSms2(Integer string) {
		sms2 = string;
	}

	public void setSms3(Integer string) {
		sms3 = string;
	}

}
