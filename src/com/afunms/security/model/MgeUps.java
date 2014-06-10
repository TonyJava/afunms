/**
 * <p>Description:mapping app_ups_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-23
 */

package com.afunms.security.model;

import com.afunms.common.base.BaseVo;

public class MgeUps extends BaseVo {
	private int id;
	private String alias;
	private String ipAddress;
	private String location;
	private String sysName;
	private String sysDescr;
	private String sysOid;
	private String type;
	private String subtype;
	private String ismanaged;
	private String community;
	private String bid;
	private String collecttype;
	private int status;

	public String getAlias() {
		return alias;
	}

	public String getBid() {
		return bid;
	}

	public String getCollecttype() {
		return collecttype;
	}

	public String getCommunity() {
		return community;
	}

	public int getId() {
		return id;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getIsmanaged() {
		return ismanaged;
	}

	public String getLocation() {
		return location;
	}

	public int getStatus() {
		return status;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getSysDescr() {
		return sysDescr;
	}

	public String getSysName() {
		return sysName;
	}

	public String getSysOid() {
		return sysOid;
	}

	public String getType() {
		return type;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public void setCollecttype(String collecttype) {
		this.collecttype = collecttype;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setIsmanaged(String ismanaged) {
		this.ismanaged = ismanaged;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setSysDescr(String sysDescr) {
		this.sysDescr = sysDescr;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public void setSysOid(String sysOid) {
		this.sysOid = sysOid;
	}

	public void setType(String type) {
		this.type = type;
	}
}