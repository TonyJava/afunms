package com.afunms.polling.om;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/** @author Hibernate CodeGenerator */
public class VMWareConnectConfig extends BaseVo implements Serializable {

	/** identifier field */
	private Long id;

	/** nullable persistent field */
	private Long nodeid;

	/** nullable persistent field */
	private String username;

	/** nullable persistent field */
	private String pwd;

	/** nullable persistent field */
	private String hosturl;

	/** nullable persistent field */
	private String bak;

	/** default constructor */
	public VMWareConnectConfig() {
	}

	public String getBak() {
		return bak;
	}

	public String getHosturl() {
		return hosturl;
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

	public String getPwd() {
		return pwd;
	}

	public String getUsername() {
		return username;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public void setHosturl(String hosturl) {
		this.hosturl = hosturl;
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

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
