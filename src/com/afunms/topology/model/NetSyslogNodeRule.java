package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class NetSyslogNodeRule extends BaseVo {
	private String id;
	private String nodeid;// �豸id
	private String facility;// ���ȼ�

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public String getFacility() {
		return facility;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
