package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNVirtualSite implements Serializable {

	private int id;

	private String ipaddress;

	private String type;

	private String subtype;

	private Calendar Collecttime;

	private String virtualSiteId;

	private int virtualSiteActiveSessions;

	private int virtualSiteSuccessLogin;

	private int virtualSiteFailureLogin;

	private int virtualSiteErrorLogin;

	private int virtualSiteSuccessLogout;

	private long virtualSiteBytesIn;

	private long virtualSiteBytesOut;

	private int virtualSiteMaxActiveSessions;

	private int virtualSiteFileAuthorizedRequests;

	private int virtualSiteFileUnauthorizedRequests;

	private int virtualSiteFileBytesIn;

	private int virtualSiteFileBytesOut;

	private int virtualSiteLockedLogin;

	private int virtualSiteRejectedLogin;

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

	public int getVirtualSiteActiveSessions() {
		return virtualSiteActiveSessions;
	}

	public long getVirtualSiteBytesIn() {
		return virtualSiteBytesIn;
	}

	public long getVirtualSiteBytesOut() {
		return virtualSiteBytesOut;
	}

	public int getVirtualSiteErrorLogin() {
		return virtualSiteErrorLogin;
	}

	public int getVirtualSiteFailureLogin() {
		return virtualSiteFailureLogin;
	}

	public int getVirtualSiteFileAuthorizedRequests() {
		return virtualSiteFileAuthorizedRequests;
	}

	public int getVirtualSiteFileBytesIn() {
		return virtualSiteFileBytesIn;
	}

	public int getVirtualSiteFileBytesOut() {
		return virtualSiteFileBytesOut;
	}

	public int getVirtualSiteFileUnauthorizedRequests() {
		return virtualSiteFileUnauthorizedRequests;
	}

	public String getVirtualSiteId() {
		return virtualSiteId;
	}

	public int getVirtualSiteLockedLogin() {
		return virtualSiteLockedLogin;
	}

	public int getVirtualSiteMaxActiveSessions() {
		return virtualSiteMaxActiveSessions;
	}

	public int getVirtualSiteRejectedLogin() {
		return virtualSiteRejectedLogin;
	}

	public int getVirtualSiteSuccessLogin() {
		return virtualSiteSuccessLogin;
	}

	public int getVirtualSiteSuccessLogout() {
		return virtualSiteSuccessLogout;
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

	public void setVirtualSiteActiveSessions(int virtualSiteActiveSessions) {
		this.virtualSiteActiveSessions = virtualSiteActiveSessions;
	}

	public void setVirtualSiteBytesIn(long virtualSiteBytesIn) {
		this.virtualSiteBytesIn = virtualSiteBytesIn;
	}

	public void setVirtualSiteBytesOut(long virtualSiteBytesOut) {
		this.virtualSiteBytesOut = virtualSiteBytesOut;
	}

	public void setVirtualSiteErrorLogin(int virtualSiteErrorLogin) {
		this.virtualSiteErrorLogin = virtualSiteErrorLogin;
	}

	public void setVirtualSiteFailureLogin(int virtualSiteFailureLogin) {
		this.virtualSiteFailureLogin = virtualSiteFailureLogin;
	}

	public void setVirtualSiteFileAuthorizedRequests(int virtualSiteFileAuthorizedRequests) {
		this.virtualSiteFileAuthorizedRequests = virtualSiteFileAuthorizedRequests;
	}

	public void setVirtualSiteFileBytesIn(int virtualSiteFileBytesIn) {
		this.virtualSiteFileBytesIn = virtualSiteFileBytesIn;
	}

	public void setVirtualSiteFileBytesOut(int virtualSiteFileBytesOut) {
		this.virtualSiteFileBytesOut = virtualSiteFileBytesOut;
	}

	public void setVirtualSiteFileUnauthorizedRequests(int virtualSiteFileUnauthorizedRequests) {
		this.virtualSiteFileUnauthorizedRequests = virtualSiteFileUnauthorizedRequests;
	}

	public void setVirtualSiteId(String virtualSiteId) {
		this.virtualSiteId = virtualSiteId;
	}

	public void setVirtualSiteLockedLogin(int virtualSiteLockedLogin) {
		this.virtualSiteLockedLogin = virtualSiteLockedLogin;
	}

	public void setVirtualSiteMaxActiveSessions(int virtualSiteMaxActiveSessions) {
		this.virtualSiteMaxActiveSessions = virtualSiteMaxActiveSessions;
	}

	public void setVirtualSiteRejectedLogin(int virtualSiteRejectedLogin) {
		this.virtualSiteRejectedLogin = virtualSiteRejectedLogin;
	}

	public void setVirtualSiteSuccessLogin(int virtualSiteSuccessLogin) {
		this.virtualSiteSuccessLogin = virtualSiteSuccessLogin;
	}

	public void setVirtualSiteSuccessLogout(int virtualSiteSuccessLogout) {
		this.virtualSiteSuccessLogout = virtualSiteSuccessLogout;
	}
}
