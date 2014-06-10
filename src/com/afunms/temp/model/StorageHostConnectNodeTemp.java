package com.afunms.temp.model;

import com.afunms.common.base.BaseVo;

public class StorageHostConnectNodeTemp extends BaseVo {

	private String nodeid;

	private String ip;

	private String name;

	private String hostconnect_id;

	private String wwpn;

	private String hostType;

	private String profile;

	private String portgrp;

	private String volgrpID;

	private String essIOport;

	private String collecttime;

	/**
	 * @return the collecttime
	 */
	public String getCollecttime() {
		return collecttime;
	}

	/**
	 * @return the essIOport
	 */
	public String getEssIOport() {
		return essIOport;
	}

	/**
	 * @return the hostconnect_id
	 */
	public String getHostconnect_id() {
		return hostconnect_id;
	}

	/**
	 * @return the hostType
	 */
	public String getHostType() {
		return hostType;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the nodeid
	 */
	public String getNodeid() {
		return nodeid;
	}

	/**
	 * @return the portgrp
	 */
	public String getPortgrp() {
		return portgrp;
	}

	/**
	 * @return the profile
	 */
	public String getProfile() {
		return profile;
	}

	/**
	 * @return the volgrpID
	 */
	public String getVolgrpID() {
		return volgrpID;
	}

	/**
	 * @return the wwpn
	 */
	public String getWwpn() {
		return wwpn;
	}

	/**
	 * @param collecttime
	 *            the collecttime to set
	 */
	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	/**
	 * @param essIOport
	 *            the essIOport to set
	 */
	public void setEssIOport(String essIOport) {
		this.essIOport = essIOport;
	}

	/**
	 * @param hostconnect_id
	 *            the hostconnect_id to set
	 */
	public void setHostconnect_id(String hostconnect_id) {
		this.hostconnect_id = hostconnect_id;
	}

	/**
	 * @param hostType
	 *            the hostType to set
	 */
	public void setHostType(String hostType) {
		this.hostType = hostType;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param nodeid
	 *            the nodeid to set
	 */
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	/**
	 * @param portgrp
	 *            the portgrp to set
	 */
	public void setPortgrp(String portgrp) {
		this.portgrp = portgrp;
	}

	/**
	 * @param profile
	 *            the profile to set
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}

	/**
	 * @param volgrpID
	 *            the volgrpID to set
	 */
	public void setVolgrpID(String volgrpID) {
		this.volgrpID = volgrpID;
	}

	/**
	 * @param wwpn
	 *            the wwpn to set
	 */
	public void setWwpn(String wwpn) {
		this.wwpn = wwpn;
	}

}
