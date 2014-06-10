package com.afunms.polling.om;

import java.io.Serializable;

public class ServiceCollectEntity implements Serializable {
	private Integer id;
	private String ipaddress;
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

	public ServiceCollectEntity() {

	}

	public ServiceCollectEntity(Integer id, String ipaddress, String name, String instate, String opstate, String uninst, String paused) {
		this.id = id;
		this.ipaddress = ipaddress;
		this.name = name;
		this.instate = instate;
		this.opstate = opstate;
		this.uninst = uninst;
		this.paused = paused;
	}

	public String getCollecttime() {
		return collecttime;
	}

	public String getDescription() {
		return description;
	}

	public String getGroupstr() {
		return groupstr;
	}

	public Integer getId() {
		return id;
	}

	public String getInstate() {
		return instate;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getName() {
		return name;
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

	public String getUninst() {
		return uninst;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setGroupstr(String groupstr) {
		this.groupstr = groupstr;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setInstate(String instate) {
		this.instate = instate;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setName(String name) {
		this.name = name;
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

	public void setUninst(String uninst) {
		this.uninst = uninst;
	}

}
