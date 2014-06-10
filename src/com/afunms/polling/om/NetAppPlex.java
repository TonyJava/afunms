package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppPlex extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private Calendar collectTime;

	private String plexIndex;

	private String plexName;

	private String plexVolName;

	private String plexStatus;

	private String plexPercentResyncing;

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getPlexIndex() {
		return plexIndex;
	}

	public String getPlexName() {
		return plexName;
	}

	public String getPlexPercentResyncing() {
		return plexPercentResyncing;
	}

	public String getPlexStatus() {
		return plexStatus;
	}

	public String getPlexVolName() {
		return plexVolName;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setPlexIndex(String plexIndex) {
		this.plexIndex = plexIndex;
	}

	public void setPlexName(String plexName) {
		this.plexName = plexName;
	}

	public void setPlexPercentResyncing(String plexPercentResyncing) {
		this.plexPercentResyncing = plexPercentResyncing;
	}

	public void setPlexStatus(String plexStatus) {
		this.plexStatus = plexStatus;
	}

	public void setPlexVolName(String plexVolName) {
		this.plexVolName = plexVolName;
	}

}
