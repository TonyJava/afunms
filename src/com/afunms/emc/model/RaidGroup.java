package com.afunms.emc.model;

import java.util.List;

import com.afunms.common.base.BaseVo;

public class RaidGroup extends BaseVo {
	private String id;
	private String nodeid;
	private String rid;
	private String type; // 类型
	private String[] state; // 状态
	private List<String> disksList; // 磁盘列表
	private List<String> lunsList; // lun列表
	private String maxNumDisk; // 磁盘最大数量
	private String maxNumLun; // LUN最大数量
	private String rawCapacity; // 原始容量（Block)
	private String logicalCapacity; // 逻辑容量（Block)
	private String freeCapacity; // 空闲容量（Block)
	private String defragPriority; // 重建优先级（尽快、高、中、低）

	private String stateStr;
	private String disklistStr;
	private String lunlistStr;

	public String getDefragPriority() {
		return defragPriority;
	}

	public String getDisklistStr() {
		return disklistStr;
	}

	public List<String> getDisksList() {
		return disksList;
	}

	public String getFreeCapacity() {
		return freeCapacity;
	}

	public String getId() {
		return id;
	}

	public String getLogicalCapacity() {
		return logicalCapacity;
	}

	public String getLunlistStr() {
		return lunlistStr;
	}

	public List<String> getLunsList() {
		return lunsList;
	}

	public String getMaxNumDisk() {
		return maxNumDisk;
	}

	public String getMaxNumLun() {
		return maxNumLun;
	}

	public String getNodeid() {
		return nodeid;
	}

	public String getRawCapacity() {
		return rawCapacity;
	}

	public String getRid() {
		return rid;
	}

	public String[] getState() {
		return state;
	}

	public String getStateStr() {
		return stateStr;
	}

	public String getType() {
		return type;
	}

	public void setDefragPriority(String defragPriority) {
		this.defragPriority = defragPriority;
	}

	public void setDisklistStr(String disklistStr) {
		this.disklistStr = disklistStr;
	}

	public void setDisksList(List<String> disksList) {
		this.disksList = disksList;
	}

	public void setFreeCapacity(String freeCapacity) {
		this.freeCapacity = freeCapacity;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLogicalCapacity(String logicalCapacity) {
		this.logicalCapacity = logicalCapacity;
	}

	public void setLunlistStr(String lunlistStr) {
		this.lunlistStr = lunlistStr;
	}

	public void setLunsList(List<String> lunsList) {
		this.lunsList = lunsList;
	}

	public void setMaxNumDisk(String maxNumDisk) {
		this.maxNumDisk = maxNumDisk;
	}

	public void setMaxNumLun(String maxNumLun) {
		this.maxNumLun = maxNumLun;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public void setRawCapacity(String rawCapacity) {
		this.rawCapacity = rawCapacity;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public void setState(String[] state) {
		this.state = state;
	}

	public void setStateStr(String stateStr) {
		this.stateStr = stateStr;
	}

	public void setType(String type) {
		this.type = type;
	}

}
