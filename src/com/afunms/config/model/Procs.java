/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.model;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

public class Procs extends BaseVo implements Serializable {
	private Integer id;
	/** nullable persistent field */
	private Integer nodeid;

	private Integer wbstatus;

	private Integer flag;

	private String ipaddress;

	/** nullable persistent field */
	private String procname;

	/** identifier field */
	private String chname;

	/** nullable persistent field */
	private String bak;

	private String bid;

	private java.util.Calendar collecttime;

	private int supperid;// π©”¶…Ãid snow add at 2010-5-21

	/** default constructor */
	public Procs() {
	}

	/** full constructor */
	public Procs(Integer nodeid, Integer wbstatus, Integer flag, String ipaddress, String procname, String chname, String bak, java.util.Calendar collecttime, int supperid) {
		this.nodeid = nodeid;
		this.wbstatus = wbstatus;
		this.flag = flag;
		this.ipaddress = ipaddress;
		this.procname = procname;
		this.chname = chname;
		this.bak = bak;
		this.collecttime = collecttime;
		this.supperid = supperid;
	}

	/**
	 * @return
	 */
	public String getBak() {
		return bak;
	}

	public String getBid() {
		return bid;
	}

	/**
	 * @return
	 */
	public String getChname() {
		return chname;
	}

	public java.util.Calendar getCollecttime() {
		return collecttime;
	}

	public Integer getFlag() {
		return flag;
	}

	public Integer getId() {
		return this.id;
	}

	/**
	 * @return
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	public Integer getNodeid() {
		return nodeid;
	}

	/**
	 * @return
	 */
	public String getProcname() {
		return procname;
	}

	public int getSupperid() {
		return supperid;
	}

	public Integer getWbstatus() {
		return wbstatus;
	}

	/**
	 * @param serializable
	 */
	public void setBak(String string) {
		bak = string;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	/**
	 * @param string
	 */
	public void setChname(String string) {
		chname = string;
	}

	public void setCollecttime(java.util.Calendar calendar) {
		collecttime = calendar;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param string
	 */
	public void setIpaddress(String string) {
		ipaddress = string;
	}

	public void setNodeid(Integer nodeid) {
		this.nodeid = nodeid;
	}

	/**
	 * @param string
	 */
	public void setProcname(String string) {
		procname = string;
	}

	public void setSupperid(int supperid) {
		this.supperid = supperid;
	}

	public void setWbstatus(Integer wbstatus) {
		this.wbstatus = wbstatus;
	}

}
