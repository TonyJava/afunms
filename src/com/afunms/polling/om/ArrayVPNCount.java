package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNCount implements Serializable {

	private int id;

	private String ipaddress;

	private String type;

	private String subtype;

	private Calendar Collecttime;

	private int virtualSiteCount;

	private int vpnCount;

	private int webCount;

	private int vclientAppCount;

	private int virtualSiteGroupCount;

	private int tcsModuleCount;

	private int imapsCount;

	private int smtpsCount;

	private int appFilterCount;

	private int dvpnSiteCount;

	private int dvpnResourceCount;

	private int dvpnTunnelCount;

	private int dvpnAclCount;

	private int maxCluster;

	private int clusterNum;

	private int rsCount;

	private int vsCount;

	private int infNumber;

	public int getAppFilterCount() {
		return appFilterCount;
	}

	public int getClusterNum() {
		return clusterNum;
	}

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public int getDvpnAclCount() {
		return dvpnAclCount;
	}

	public int getDvpnResourceCount() {
		return dvpnResourceCount;
	}

	public int getDvpnSiteCount() {
		return dvpnSiteCount;
	}

	public int getDvpnTunnelCount() {
		return dvpnTunnelCount;
	}

	public int getId() {
		return id;
	}

	public int getImapsCount() {
		return imapsCount;
	}

	public int getInfNumber() {
		return infNumber;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public int getMaxCluster() {
		return maxCluster;
	}

	public int getRsCount() {
		return rsCount;
	}

	public int getSmtpsCount() {
		return smtpsCount;
	}

	public String getSubtype() {
		return subtype;
	}

	public int getTcsModuleCount() {
		return tcsModuleCount;
	}

	public String getType() {
		return type;
	}

	public int getVclientAppCount() {
		return vclientAppCount;
	}

	public int getVirtualSiteCount() {
		return virtualSiteCount;
	}

	public int getVirtualSiteGroupCount() {
		return virtualSiteGroupCount;
	}

	public int getVpnCount() {
		return vpnCount;
	}

	public int getVsCount() {
		return vsCount;
	}

	public int getWebCount() {
		return webCount;
	}

	public void setAppFilterCount(int appFilterCount) {
		this.appFilterCount = appFilterCount;
	}

	public void setClusterNum(int clusterNum) {
		this.clusterNum = clusterNum;
	}

	public void setCollecttime(Calendar collecttime) {
		Collecttime = collecttime;
	}

	public void setDvpnAclCount(int dvpnAclCount) {
		this.dvpnAclCount = dvpnAclCount;
	}

	public void setDvpnResourceCount(int dvpnResourceCount) {
		this.dvpnResourceCount = dvpnResourceCount;
	}

	public void setDvpnSiteCount(int dvpnSiteCount) {
		this.dvpnSiteCount = dvpnSiteCount;
	}

	public void setDvpnTunnelCount(int dvpnTunnelCount) {
		this.dvpnTunnelCount = dvpnTunnelCount;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setImapsCount(int imapsCount) {
		this.imapsCount = imapsCount;
	}

	public void setInfNumber(int infNumber) {
		this.infNumber = infNumber;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setMaxCluster(int maxCluster) {
		this.maxCluster = maxCluster;
	}

	public void setRsCount(int rsCount) {
		this.rsCount = rsCount;
	}

	public void setSmtpsCount(int smtpsCount) {
		this.smtpsCount = smtpsCount;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setTcsModuleCount(int tcsModuleCount) {
		this.tcsModuleCount = tcsModuleCount;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setVclientAppCount(int vclientAppCount) {
		this.vclientAppCount = vclientAppCount;
	}

	public void setVirtualSiteCount(int virtualSiteCount) {
		this.virtualSiteCount = virtualSiteCount;
	}

	public void setVirtualSiteGroupCount(int virtualSiteGroupCount) {
		this.virtualSiteGroupCount = virtualSiteGroupCount;
	}

	public void setVpnCount(int vpnCount) {
		this.vpnCount = vpnCount;
	}

	public void setVsCount(int vsCount) {
		this.vsCount = vsCount;
	}

	public void setWebCount(int webCount) {
		this.webCount = webCount;
	}

}
