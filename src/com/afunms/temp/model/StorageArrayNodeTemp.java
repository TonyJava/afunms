package com.afunms.temp.model;

import com.afunms.common.base.BaseVo;

public class StorageArrayNodeTemp extends BaseVo {

	private String nodeid;

	private String ip;

	private String array;

	private String state;

	private String data;

	private String raidtype;

	private String arsite;

	private String rank;

	private String da_pair;

	private String ddmcap;

	private String collecttime;

	/**
	 * @return the array
	 */
	public String getArray() {
		return array;
	}

	/**
	 * @return the arsite
	 */
	public String getArsite() {
		return arsite;
	}

	/**
	 * @return the collecttime
	 */
	public String getCollecttime() {
		return collecttime;
	}

	/**
	 * @return the da_pair
	 */
	public String getDa_pair() {
		return da_pair;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @return the ddmcap
	 */
	public String getDdmcap() {
		return ddmcap;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return the nodeid
	 */
	public String getNodeid() {
		return nodeid;
	}

	/**
	 * @return the raidtype
	 */
	public String getRaidtype() {
		return raidtype;
	}

	/**
	 * @return the rank
	 */
	public String getRank() {
		return rank;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param array
	 *            the array to set
	 */
	public void setArray(String array) {
		this.array = array;
	}

	/**
	 * @param arsite
	 *            the arsite to set
	 */
	public void setArsite(String arsite) {
		this.arsite = arsite;
	}

	/**
	 * @param collecttime
	 *            the collecttime to set
	 */
	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	/**
	 * @param da_pair
	 *            the da_pair to set
	 */
	public void setDa_pair(String da_pair) {
		this.da_pair = da_pair;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * @param ddmcap
	 *            the ddmcap to set
	 */
	public void setDdmcap(String ddmcap) {
		this.ddmcap = ddmcap;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @param nodeid
	 *            the nodeid to set
	 */
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	/**
	 * @param raidtype
	 *            the raidtype to set
	 */
	public void setRaidtype(String raidtype) {
		this.raidtype = raidtype;
	}

	/**
	 * @param rank
	 *            the rank to set
	 */
	public void setRank(String rank) {
		this.rank = rank;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

}
