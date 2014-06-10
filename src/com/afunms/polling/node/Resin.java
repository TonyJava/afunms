package com.afunms.polling.node;

public class Resin extends Application {
	private String user;
	private String password;
	private String port;

	private String bid;
	private int monflag;
	private String sendmobiles;
	private String sendemail;
	private String sendphone;

	private String version;
	private String jvmversion;
	private String jvmvender;
	private String os;
	private String osversion;
	private String lastAlarm;

	public Resin() {
		category = 72;
	}

	@Override
	public String getBid() {
		return bid;
	}

	public String getJspUrl() {
		return "http://" + getIpAddress() + ":" + getPort() + "/manager/tomcat_monitor.jsp";
	}

	public String getJvmvender() {
		return jvmvender;
	}

	public String getJvmversion() {
		return jvmversion;
	}

	@Override
	public String getLastAlarm() {
		return lastAlarm;
	}

	public int getMonflag() {
		return monflag;
	}

	public String getOs() {
		return os;
	}

	public String getOsversion() {
		return osversion;
	}

	public String getPassword() {
		return password;
	}

	public String getPort() {
		return port;
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

	public String getUser() {
		return user;
	}

	public String getVersion() {
		return version;
	}

	public String getXmlUrl() {
		return "http://" + getIpAddress() + ":" + getPort() + "/manager/tomcat_monitor.xml";
	}

	@Override
	public void setBid(String bid) {
		this.bid = bid;
	}

	public void setJvmvender(String jvmvender) {
		this.jvmvender = jvmvender;
	}

	public void setJvmversion(String jvmversion) {
		this.jvmversion = jvmversion;
	}

	@Override
	public void setLastAlarm(String lastAlarm) {
		this.lastAlarm = lastAlarm;
	}

	public void setMonflag(int monflag) {
		this.monflag = monflag;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public void setOsversion(String osversion) {
		this.osversion = osversion;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPort(String port) {
		this.port = port;
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

	public void setUser(String user) {
		this.user = user;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}