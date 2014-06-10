package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppTree extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	private String ipaddress;

	private String treeIndex;

	private String treeVolume;

	private String treeVolumeName;

	private String treeId;

	private String treeName;

	private String treeStatus;

	private String treeStyle;

	private String treeOpLocks;

	private Calendar collectTime;

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getTreeId() {
		return treeId;
	}

	public String getTreeIndex() {
		return treeIndex;
	}

	public String getTreeName() {
		return treeName;
	}

	public String getTreeOpLocks() {
		return treeOpLocks;
	}

	public String getTreeStatus() {
		return treeStatus;
	}

	public String getTreeStyle() {
		return treeStyle;
	}

	public String getTreeVolume() {
		return treeVolume;
	}

	public String getTreeVolumeName() {
		return treeVolumeName;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}

	public void setTreeIndex(String treeIndex) {
		this.treeIndex = treeIndex;
	}

	public void setTreeName(String treeName) {
		this.treeName = treeName;
	}

	public void setTreeOpLocks(String treeOpLocks) {
		this.treeOpLocks = treeOpLocks;
	}

	public void setTreeStatus(String treeStatus) {
		this.treeStatus = treeStatus;
	}

	public void setTreeStyle(String treeStyle) {
		this.treeStyle = treeStyle;
	}

	public void setTreeVolume(String treeVolume) {
		this.treeVolume = treeVolume;
	}

	public void setTreeVolumeName(String treeVolumeName) {
		this.treeVolumeName = treeVolumeName;
	}

}
