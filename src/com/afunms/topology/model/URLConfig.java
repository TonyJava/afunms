package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class URLConfig extends BaseVo {
	private int id;
	private int nodeID;
	private String alias;
	private String url;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}
}