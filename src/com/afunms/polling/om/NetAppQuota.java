package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppQuota extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress; // ≈‰∂ÓId

	private String quotaId; // ≈‰∂ÓId

	private String quotaName;// ≈‰∂Ó√˚◊÷

	private String quotaState;// ≈‰∂Ó◊¥Ã¨

	private String quotaInitPercent;// ≈‰∂Ó≥ı ºªØ±»¿˝

	private Calendar collectTime; // ≈‰∂Ó≤…ºØ ±º‰

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getQuotaId() {
		return quotaId;
	}

	public String getQuotaInitPercent() {
		return quotaInitPercent;
	}

	public String getQuotaName() {
		return quotaName;
	}

	public String getQuotaState() {
		return quotaState;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setQuotaId(String quotaId) {
		this.quotaId = quotaId;
	}

	public void setQuotaInitPercent(String quotaInitPercent) {
		this.quotaInitPercent = quotaInitPercent;
	}

	public void setQuotaName(String quotaName) {
		this.quotaName = quotaName;
	}

	public void setQuotaState(String quotaState) {
		this.quotaState = quotaState;
	}

}
