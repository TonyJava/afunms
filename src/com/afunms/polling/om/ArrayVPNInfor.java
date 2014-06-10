package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNInfor implements Serializable {

	private int id;

	private String ipaddress;

	private String type;

	private String subtype;

	private Calendar Collecttime;

	private String vpnId;

	private int vpnTunnelsOpen;

	private int vpnTunnelsEst;

	private int vpnTunnelsRejected;

	private int vpnTunnelsTerminated;

	private long vpnBytesIn;

	private long vpnBytesOut;

	private long vpnUnauthPacketsIn;

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getType() {
		return type;
	}

	public long getVpnBytesIn() {
		return vpnBytesIn;
	}

	public long getVpnBytesOut() {
		return vpnBytesOut;
	}

	public String getVpnId() {
		return vpnId;
	}

	public int getVpnTunnelsEst() {
		return vpnTunnelsEst;
	}

	public int getVpnTunnelsOpen() {
		return vpnTunnelsOpen;
	}

	public int getVpnTunnelsRejected() {
		return vpnTunnelsRejected;
	}

	public int getVpnTunnelsTerminated() {
		return vpnTunnelsTerminated;
	}

	public long getVpnUnauthPacketsIn() {
		return vpnUnauthPacketsIn;
	}

	public void setCollecttime(Calendar collecttime) {
		Collecttime = collecttime;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setVpnBytesIn(long vpnBytesIn) {
		this.vpnBytesIn = vpnBytesIn;
	}

	public void setVpnBytesOut(long vpnBytesOut) {
		this.vpnBytesOut = vpnBytesOut;
	}

	public void setVpnId(String vpnId) {
		this.vpnId = vpnId;
	}

	public void setVpnTunnelsEst(int vpnTunnelsEst) {
		this.vpnTunnelsEst = vpnTunnelsEst;
	}

	public void setVpnTunnelsOpen(int vpnTunnelsOpen) {
		this.vpnTunnelsOpen = vpnTunnelsOpen;
	}

	public void setVpnTunnelsRejected(int vpnTunnelsRejected) {
		this.vpnTunnelsRejected = vpnTunnelsRejected;
	}

	public void setVpnTunnelsTerminated(int vpnTunnelsTerminated) {
		this.vpnTunnelsTerminated = vpnTunnelsTerminated;
	}

	public void setVpnUnauthPacketsIn(long vpnUnauthPacketsIn) {
		this.vpnUnauthPacketsIn = vpnUnauthPacketsIn;
	}
}
