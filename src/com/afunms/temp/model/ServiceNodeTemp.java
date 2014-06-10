package com.afunms.temp.model;

import com.afunms.common.base.BaseVo;

public class ServiceNodeTemp extends BaseVo {

	private String nodeid;

	private String ip;

	private String type;

	private String subtype;

	private String name;

	private String instate;

	private String opstate;

	private String uninst;

	private String paused;

	/**
	 * 启动模式
	 */
	private String startMode;

	/**
	 * 路径
	 */
	private String pathName;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 服务类型
	 */
	private String serviceType;

	private String pid;

	private String groupstr;

	private String collecttime;

	public String getCollecttime() {
		return collecttime;
	}

	public String getDescription() {
		return description;
	}

	public String getGroupstr() {
		return groupstr;
	}

	public String getInstate() {
		return instate;
	}

	public String getIp() {
		return ip;
	}

	public String getName() {
		return name;
	}

	public String getNodeid() {
		return nodeid;
	}

	public String getOpstate() {
		return opstate;
	}

	public String getPathName() {
		return pathName;
	}

	public String getPaused() {
		return paused;
	}

	public String getPid() {
		return pid;
	}

	public String getServiceType() {
		return serviceType;
	}

	public String getStartMode() {
		return startMode;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getType() {
		return type;
	}

	public String getUninst() {
		return uninst;
	}

	public void setCollecttime(String string) {
		this.collecttime = string;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setGroupstr(String groupstr) {
		this.groupstr = groupstr;
	}

	public void setInstate(String instate) {
		this.instate = instate;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public void setOpstate(String opstate) {
		this.opstate = opstate;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public void setPaused(String paused) {
		this.paused = paused;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public void setStartMode(String startMode) {
		this.startMode = startMode;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUninst(String uninst) {
		this.uninst = uninst;
	}

}
