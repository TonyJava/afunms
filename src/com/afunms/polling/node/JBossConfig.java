/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;


public class JBossConfig extends Application {

	private int id;

	private String username;

	private String password;

	private String ipaddress;

	private int port;

	private int flag;

	private String sendmobiles;

	private String sendemail;

	private String netid;

	private String sendphone;

	public int getFlag() {
		return flag;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getNetid() {
		return netid;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
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

	public String getUsername() {
		return username;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setNetid(String netid) {
		this.netid = netid;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPort(int port) {
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

	public void setUsername(String username) {
		this.username = username;
	}

}