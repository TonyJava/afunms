package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class NodeEquip extends BaseVo {
	private int id;

	private String nodeId;

	private int equipId;

	private String xmlName;

	public int getEquipId() {
		return equipId;
	}

	public void setEquipId(int equipId) {
		this.equipId = equipId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getXmlName() {
		return xmlName;
	}

	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}

}