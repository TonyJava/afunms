package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppVFilerProtocolEntity extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private Calendar collectTime;

	private String vfFpIndex;

	private String vfProIndex;

	private String vfProName;

	private String vfProStatus;

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getVfFpIndex() {
		return vfFpIndex;
	}

	public String getVfProIndex() {
		return vfProIndex;
	}

	public String getVfProName() {
		return vfProName;
	}

	public String getVfProStatus() {
		return vfProStatus;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setVfFpIndex(String vfFpIndex) {
		this.vfFpIndex = vfFpIndex;
	}

	public void setVfProIndex(String vfProIndex) {
		this.vfProIndex = vfProIndex;
	}

	public void setVfProName(String vfProName) {
		this.vfProName = vfProName;
	}

	public void setVfProStatus(String vfProStatus) {
		this.vfProStatus = vfProStatus;
	}

}
