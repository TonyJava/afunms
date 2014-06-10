/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class IpAlias extends BaseVo {
	private String id;
	private String ipaddress;
	private String aliasip;
	private String indexs;
	private String descr;
	private String speed;
	private String types;

	public String getAliasip() {
		return aliasip;
	}

	public String getDescr() {
		return descr;
	}

	public String getId() {
		return id;
	}

	public String getIndexs() {
		return indexs;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getSpeed() {
		return speed;
	}

	public String getTypes() {
		return types;
	}

	public void setAliasip(String aliasip) {
		this.aliasip = aliasip;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIndexs(String indexs) {
		this.indexs = indexs;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public void setTypes(String types) {
		this.types = types;
	}
}
