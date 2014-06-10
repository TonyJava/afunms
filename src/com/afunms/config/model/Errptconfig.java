package com.afunms.config.model;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/** @author Hibernate CodeGenerator */
public class Errptconfig extends BaseVo implements Serializable {
	private Integer id;

	private Integer nodeid;

	private String errpttype;

	private String errptclass;

	private String alarmwayid;

	public String getAlarmwayid() {
		return alarmwayid;
	}

	public String getErrptclass() {
		return errptclass;
	}

	public String getErrpttype() {
		return errpttype;
	}

	public Integer getId() {
		return id;
	}

	public Integer getNodeid() {
		return nodeid;
	}

	public void setAlarmwayid(String alarmwayid) {
		this.alarmwayid = alarmwayid;
	}

	public void setErrptclass(String errptclass) {
		this.errptclass = errptclass;
	}

	public void setErrpttype(String errpttype) {
		this.errpttype = errpttype;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNodeid(Integer nodeid) {
		this.nodeid = nodeid;
	}

}
