package com.afunms.temp.model;

import com.afunms.common.base.BaseVo;

public class StorageVolgrpFbvolNodeTemp extends BaseVo {

	private String nodeid;

	private String ip;

	private String name;

	private String volgrp_id;

	private String type;

	private String vols;

	private String collecttime;

	/**
	 * @return the collecttime
	 */
	public String getCollecttime() {
		return collecttime;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the volgrp_id
	 */
	public String getVolgrp_id() {
		return volgrp_id;
	}

	/**
	 * @return the vols
	 */
	public String getVols() {
		return vols;
	}

	/**
	 * @param collecttime
	 *            the collecttime to set
	 */
	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
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
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param volgrp_id
	 *            the volgrp_id to set
	 */
	public void setVolgrp_id(String volgrp_id) {
		this.volgrp_id = volgrp_id;
	}

	/**
	 * @param vols
	 *            the vols to set
	 */
	public void setVols(String vols) {
		this.vols = vols;
	}

}
