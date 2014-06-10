/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;


public class Weblogic extends Application {

	private int id;
	// private String alias;
	// private String ipAddress;
	private String community;
	private int portnum;
	private String sendmobiles;
	private int mon_flag;
	private String netid;
	private String sendemail;
	private String sendphone;
	private String serverName;
	private String serverAddr;
	private String serverPort;
	private String domainName;
	private String domainPort;
	private String domainVersion;

	public String getCommunity() {
		return community;
	}

	public String getDomainName() {
		return domainName;
	}

	public String getDomainPort() {
		return domainPort;
	}

	public String getDomainVersion() {
		return domainVersion;
	}

	@Override
	public int getId() {
		return id;
	}

	public int getMon_flag() {
		return mon_flag;
	}

	public String getNetid() {
		return netid;
	}

	public int getPortnum() {
		return portnum;
	}

	@Override
	public String getSendemail() {
		return sendemail;
	}

	@Override
	public String getSendmobiles() {
		return sendmobiles;
	}

	@Override
	public String getSendphone() {
		return sendphone;
	}

	public String getServerAddr() {
		return serverAddr;
	}

	public String getServerName() {
		return serverName;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public void setDomainPort(String domainPort) {
		this.domainPort = domainPort;
	}

	public void setDomainVersion(String domainVersion) {
		this.domainVersion = domainVersion;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public void setMon_flag(int mon_flag) {
		this.mon_flag = mon_flag;
	}

	public void setNetid(String netid) {
		this.netid = netid;
	}

	public void setPortnum(int portnum) {
		this.portnum = portnum;
	}

	@Override
	public void setSendemail(String sendemail) {
		this.sendemail = sendemail;
	}

	@Override
	public void setSendmobiles(String sendmobiles) {
		this.sendmobiles = sendmobiles;
	}

	@Override
	public void setSendphone(String sendphone) {
		this.sendphone = sendphone;
	}

	public void setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

}