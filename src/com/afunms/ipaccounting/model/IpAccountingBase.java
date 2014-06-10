package com.afunms.ipaccounting.model;

import com.afunms.common.base.BaseVo;

public class IpAccountingBase extends BaseVo {
	private int id;
	private int nodeid;
	private String srcip;
	private String destip;
	private String protocol;

	public String getDestip() {
		return destip;
	}

	public int getId() {
		return id;
	}

	public int getNodeid() {
		return nodeid;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getSrcip() {
		return srcip;
	}

	public void setDestip(String destip) {
		this.destip = destip;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setSrcip(String srcip) {
		this.srcip = srcip;
	}

}
