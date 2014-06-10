package com.afunms.temp.model;

import com.afunms.common.base.BaseVo;

public class StorageExtpoolNodeTemp extends BaseVo {

	private String nodeid;

	private String ip;

	private String name;

	private String extpool_id;

	private String stgtype;

	private String rankgrp;

	private String status;

	private String availstor;

	private String allocated;

	private String available;

	private String reserved;

	private String numvols;

	private String collecttime;

	/**
	 * @return the allocated
	 */
	public String getAllocated() {
		return allocated;
	}

	/**
	 * @return the available
	 */
	public String getAvailable() {
		return available;
	}

	/**
	 * @return the availstor
	 */
	public String getAvailstor() {
		return availstor;
	}

	/**
	 * @return the collecttime
	 */
	public String getCollecttime() {
		return collecttime;
	}

	/**
	 * @return the extpool_id
	 */
	public String getExtpool_id() {
		return extpool_id;
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
	 * @return the numvols
	 */
	public String getNumvols() {
		return numvols;
	}

	/**
	 * @return the rankgrp
	 */
	public String getRankgrp() {
		return rankgrp;
	}

	/**
	 * @return the reserved
	 */
	public String getReserved() {
		return reserved;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the stgtype
	 */
	public String getStgtype() {
		return stgtype;
	}

	/**
	 * @param allocated
	 *            the allocated to set
	 */
	public void setAllocated(String allocated) {
		this.allocated = allocated;
	}

	/**
	 * @param available
	 *            the available to set
	 */
	public void setAvailable(String available) {
		this.available = available;
	}

	/**
	 * @param availstor
	 *            the availstor to set
	 */
	public void setAvailstor(String availstor) {
		this.availstor = availstor;
	}

	/**
	 * @param collecttime
	 *            the collecttime to set
	 */
	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	/**
	 * @param extpool_id
	 *            the extpool_id to set
	 */
	public void setExtpool_id(String extpool_id) {
		this.extpool_id = extpool_id;
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
	 * @param numvols
	 *            the numvols to set
	 */
	public void setNumvols(String numvols) {
		this.numvols = numvols;
	}

	/**
	 * @param rankgrp
	 *            the rankgrp to set
	 */
	public void setRankgrp(String rankgrp) {
		this.rankgrp = rankgrp;
	}

	/**
	 * @param reserved
	 *            the reserved to set
	 */
	public void setReserved(String reserved) {
		this.reserved = reserved;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @param stgtype
	 *            the stgtype to set
	 */
	public void setStgtype(String stgtype) {
		this.stgtype = stgtype;
	}

}
