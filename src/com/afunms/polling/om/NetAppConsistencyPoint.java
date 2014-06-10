package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppConsistencyPoint extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private Calendar collectTime;

	private String cpTime;

	private String cpFromTimerOps;

	private String cpFromSnapshotOps;

	private String cpFromLowWaterOps;

	private String cpFromHighWaterOps;

	private String cpFromLogFullOps;

	private String cpFromCpOps;

	private String cpTotalOps;

	private String cpFromFlushOps;

	private String cpFromSyncOps;

	private String cpFromLowVbufOps;

	private String cpFromCpDeferredOps;

	private String cpFromLowDatavecsOps;

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getCpFromCpDeferredOps() {
		return cpFromCpDeferredOps;
	}

	public String getCpFromCpOps() {
		return cpFromCpOps;
	}

	public String getCpFromFlushOps() {
		return cpFromFlushOps;
	}

	public String getCpFromHighWaterOps() {
		return cpFromHighWaterOps;
	}

	public String getCpFromLogFullOps() {
		return cpFromLogFullOps;
	}

	public String getCpFromLowDatavecsOps() {
		return cpFromLowDatavecsOps;
	}

	public String getCpFromLowVbufOps() {
		return cpFromLowVbufOps;
	}

	public String getCpFromLowWaterOps() {
		return cpFromLowWaterOps;
	}

	public String getCpFromSnapshotOps() {
		return cpFromSnapshotOps;
	}

	public String getCpFromSyncOps() {
		return cpFromSyncOps;
	}

	public String getCpFromTimerOps() {
		return cpFromTimerOps;
	}

	public String getCpTime() {
		return cpTime;
	}

	public String getCpTotalOps() {
		return cpTotalOps;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setCpFromCpDeferredOps(String cpFromCpDeferredOps) {
		this.cpFromCpDeferredOps = cpFromCpDeferredOps;
	}

	public void setCpFromCpOps(String cpFromCpOps) {
		this.cpFromCpOps = cpFromCpOps;
	}

	public void setCpFromFlushOps(String cpFromFlushOps) {
		this.cpFromFlushOps = cpFromFlushOps;
	}

	public void setCpFromHighWaterOps(String cpFromHighWaterOps) {
		this.cpFromHighWaterOps = cpFromHighWaterOps;
	}

	public void setCpFromLogFullOps(String cpFromLogFullOps) {
		this.cpFromLogFullOps = cpFromLogFullOps;
	}

	public void setCpFromLowDatavecsOps(String cpFromLowDatavecsOps) {
		this.cpFromLowDatavecsOps = cpFromLowDatavecsOps;
	}

	public void setCpFromLowVbufOps(String cpFromLowVbufOps) {
		this.cpFromLowVbufOps = cpFromLowVbufOps;
	}

	public void setCpFromLowWaterOps(String cpFromLowWaterOps) {
		this.cpFromLowWaterOps = cpFromLowWaterOps;
	}

	public void setCpFromSnapshotOps(String cpFromSnapshotOps) {
		this.cpFromSnapshotOps = cpFromSnapshotOps;
	}

	public void setCpFromSyncOps(String cpFromSyncOps) {
		this.cpFromSyncOps = cpFromSyncOps;
	}

	public void setCpFromTimerOps(String cpFromTimerOps) {
		this.cpFromTimerOps = cpFromTimerOps;
	}

	public void setCpTime(String cpTime) {
		this.cpTime = cpTime;
	}

	public void setCpTotalOps(String cpTotalOps) {
		this.cpTotalOps = cpTotalOps;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

}
