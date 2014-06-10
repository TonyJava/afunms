package com.afunms.polling.om;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/** @author Hibernate CodeGenerator */
public class VMWareVid extends BaseVo implements Serializable {

	/** identifier field */
	private Long id;

	/** nullable persistent field */
	private Long nodeid;

	/** nullable persistent field */
	private String vid;

	/** nullable persistent field */
	private String guestname;
	/** nullable persistent field */
	private String bak;

	private String hoid;

	private String flag;

	private String category;

	/** default constructor */
	public VMWareVid() {
	}

	public String getBak() {
		return bak;
	}

	public String getCategory() {
		return category;
	}

	public String getFlag() {
		return flag;
	}

	public String getGuestname() {
		return guestname;
	}

	public String getHoid() {
		return hoid;
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

	public String getVid() {
		return vid;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setGuestname(String guestname) {
		this.guestname = guestname;
	}

	public void setHoid(String hoid) {
		this.hoid = hoid;
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

	public void setVid(String vid) {
		this.vid = vid;
	}

}
