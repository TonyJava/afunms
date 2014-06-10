package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class PolicyInterface extends BaseVo {
	private int id;
	private String interfaceName;
	private String policyName;
	private String className;
	private int offeredRate;
	private int dropRate;
	private String matchGroup;
	private int matchedPkts;
	private int matchedBytes;
	private int dropsTotal;
	private int dropsBytes;
	private int depth;
	private int totalQueued;
	private int noBufferDrop;
	private String collecttime;

	public String getClassName() {
		return className;
	}

	public String getCollecttime() {
		return collecttime;
	}

	public int getDepth() {
		return depth;
	}

	public int getDropRate() {
		return dropRate;
	}

	public int getDropsBytes() {
		return dropsBytes;
	}

	public int getDropsTotal() {
		return dropsTotal;
	}

	public int getId() {
		return id;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public int getMatchedBytes() {
		return matchedBytes;
	}

	public int getMatchedPkts() {
		return matchedPkts;
	}

	public String getMatchGroup() {
		return matchGroup;
	}

	public int getNoBufferDrop() {
		return noBufferDrop;
	}

	public int getOfferedRate() {
		return offeredRate;
	}

	public String getPolicyName() {
		return policyName;
	}

	public int getTotalQueued() {
		return totalQueued;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void setDropRate(int dropRate) {
		this.dropRate = dropRate;
	}

	public void setDropsBytes(int dropsBytes) {
		this.dropsBytes = dropsBytes;
	}

	public void setDropsTotal(int dropsTotal) {
		this.dropsTotal = dropsTotal;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public void setMatchedBytes(int matchedBytes) {
		this.matchedBytes = matchedBytes;
	}

	public void setMatchedPkts(int matchedPkts) {
		this.matchedPkts = matchedPkts;
	}

	public void setMatchGroup(String matchGroup) {
		this.matchGroup = matchGroup;
	}

	public void setNoBufferDrop(int noBufferDrop) {
		this.noBufferDrop = noBufferDrop;
	}

	public void setOfferedRate(int offeredRate) {
		this.offeredRate = offeredRate;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public void setTotalQueued(int totalQueued) {
		this.totalQueued = totalQueued;
	}

}
