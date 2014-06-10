/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.model;

import com.afunms.common.base.BaseVo;

public class EventList extends BaseVo implements java.io.Serializable {
	private Integer id;
	private String eventtype;
	private String eventlocation;
	private String content;
	private Integer level1;
	private Integer managesign; // 0:未处理 1：处理中 2：已完成
	private String bak;
	private java.util.Calendar recordtime;
	private String lasttime;
	private String reportman;
	private Integer nodeid;
	private String businessid;
	private int oid;
	private String subtype; // network,host,db,web
	private String managetime;
	private String subentity;
	private int alarmlevel;// 警告等级
	private int happenTimes;// 警告等级

	public int getAlarmlevel() {
		return alarmlevel;
	}

	public java.lang.String getBak() {
		return this.bak;
	}

	public java.lang.String getBusinessid() {
		return this.businessid;
	}

	public java.lang.String getContent() {
		return this.content;
	}

	public java.lang.String getEventlocation() {
		return this.eventlocation;
	}

	public java.lang.String getEventtype() {
		return this.eventtype;
	}

	public Integer getId() {
		return this.id;
	}

	public String getLasttime() {
		return lasttime;
	}

	public Integer getLevel1() {
		return this.level1;
	}

	public Integer getManagesign() {
		return this.managesign;
	}

	public String getManagetime() {
		return this.managetime;
	}

	public Integer getNodeid() {
		return this.nodeid;
	}

	public Integer getOid() {
		return this.oid;
	}

	public java.util.Calendar getRecordtime() {
		return this.recordtime;
	}

	public java.lang.String getReportman() {
		return this.reportman;
	}

	public String getSubentity() {
		return this.subentity;
	}

	public java.lang.String getSubtype() {
		return this.subtype;
	}

	public void setAlarmlevel(int alarmlevel) {
		this.alarmlevel = alarmlevel;
	}

	public void setBak(java.lang.String bak) {
		this.bak = bak;
	}

	public void setBusinessid(java.lang.String businessid) {
		this.businessid = businessid;
	}

	public void setContent(java.lang.String content) {
		this.content = content;
	}

	public void setEventlocation(java.lang.String eventlocation) {
		this.eventlocation = eventlocation;
	}

	public void setEventtype(java.lang.String eventtype) {
		this.eventtype = eventtype;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}

	public void setLevel1(Integer level) {
		this.level1 = level;
	}

	public void setManagesign(Integer managesign) {
		this.managesign = managesign;
	}

	public void setManagetime(String managetime) {
		this.managetime = managetime;
	}

	public void setNodeid(Integer nodeid) {
		this.nodeid = nodeid;
	}

	public void setOid(Integer oid) {
		this.oid = oid;
	}

	public void setRecordtime(java.util.Calendar recordtime) {
		this.recordtime = recordtime;
	}

	public void setReportman(java.lang.String reportman) {
		this.reportman = reportman;
	}

	public void setSubentity(String subentity) {
		this.subentity = subentity;
	}

	public void setSubtype(java.lang.String subtype) {
		this.subtype = subtype;
	}

	public int getHappenTimes() {
		return happenTimes;
	}

	public void setHappenTimes(int happenTimes) {
		this.happenTimes = happenTimes;
	}

}
