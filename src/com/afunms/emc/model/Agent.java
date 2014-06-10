package com.afunms.emc.model;

import com.afunms.common.base.BaseVo;

public class Agent extends BaseVo {

	private String id;
	private String nodeid;
	private String agentRev;// 版本
	private String name;// 名称
	private String descr;// 描述
	private String node;// 设备在文件系统中的位置
	private String physicalNode;// 物理位置
	private String Signature;// sp签名
	private String peerSignature;// 对等sp签名
	private String revision;// flare微码版本号
	private String SCSIId;// sp连接的主机的scsi id
	private String model;// sp型号
	private String modelType;// sp型号类型
	private String promRev;// sp rrom版本
	private String SPMemory;// sp内存
	private String serialNo;// 序列号
	private String SPIdentifier;// 标识
	private String cabinet;// 机架类型

	public String getAgentRev() {
		return agentRev;
	}

	public String getCabinet() {
		return cabinet;
	}

	public String getDescr() {
		return descr;
	}

	public String getId() {
		return id;
	}

	public String getModel() {
		return model;
	}

	public String getModelType() {
		return modelType;
	}

	public String getName() {
		return name;
	}

	public String getNode() {
		return node;
	}

	public String getNodeid() {
		return nodeid;
	}

	public String getPeerSignature() {
		return peerSignature;
	}

	public String getPhysicalNode() {
		return physicalNode;
	}

	public String getPromRev() {
		return promRev;
	}

	public String getRevision() {
		return revision;
	}

	public String getSCSIId() {
		return SCSIId;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public String getSignature() {
		return Signature;
	}

	public String getSPIdentifier() {
		return SPIdentifier;
	}

	public String getSPMemory() {
		return SPMemory;
	}

	public void setAgentRev(String agentRev) {
		this.agentRev = agentRev;
	}

	public void setCabinet(String cabinet) {
		this.cabinet = cabinet;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public void setPeerSignature(String peerSignature) {
		this.peerSignature = peerSignature;
	}

	public void setPhysicalNode(String physicalNode) {
		this.physicalNode = physicalNode;
	}

	public void setPromRev(String promRev) {
		this.promRev = promRev;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public void setSCSIId(String id) {
		SCSIId = id;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public void setSignature(String signature) {
		Signature = signature;
	}

	public void setSPIdentifier(String identifier) {
		SPIdentifier = identifier;
	}

	public void setSPMemory(String memory) {
		SPMemory = memory;
	}

}
