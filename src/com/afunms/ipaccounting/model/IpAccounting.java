package com.afunms.ipaccounting.model;

import com.afunms.common.base.BaseVo;

public class IpAccounting extends BaseVo {
	private int id;
	private int accountingBaseID;
	private String srcip;
	private String destip;
	private int pkts;
	private int byts;
	private java.util.Calendar collecttime;

	public int getAccountingBaseID() {
		return accountingBaseID;
	}

	public int getByts() {
		return byts;
	}

	public java.util.Calendar getCollecttime() {
		return collecttime;
	}

	public String getDestip() {
		return destip;
	}

	public int getId() {
		return id;
	}

	public int getPkts() {
		return pkts;
	}

	public String getSrcip() {
		return srcip;
	}

	public void setAccountingBaseID(int accountingBaseID) {
		this.accountingBaseID = accountingBaseID;
	}

	public void setByts(int byts) {
		this.byts = byts;
	}

	public void setCollecttime(java.util.Calendar collecttime) {
		this.collecttime = collecttime;
	}

	public void setDestip(String destip) {
		this.destip = destip;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPkts(int pkts) {
		this.pkts = pkts;
	}

	public void setSrcip(String srcip) {
		this.srcip = srcip;
	}

}