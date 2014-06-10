/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;


public class Mail extends Application {

	private int id;
	private String name;
	private String address;
	private String ipaddress;
	private String username;
	private String password;
	private String recivemail;
	private int timeout;
	private int flag;
	private int monflag;
	private String sendmobiles;
	private String bid;
	private String sendemail;
	private String sendphone;

	public String getAddress() {
		return address;
	}

	@Override
	public String getBid() {
		return bid;
	}

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

	public int getMonflag() {
		return monflag;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getRecivemail() {
		return recivemail;
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

	public int getTimeout() {
		return timeout;
	}

	public String getUsername() {
		return username;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public void setBid(String bid) {
		this.bid = bid;
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

	public void setMonflag(int monflag) {
		this.monflag = monflag;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRecivemail(String recivemail) {
		this.recivemail = recivemail;
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

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}