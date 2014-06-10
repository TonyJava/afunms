package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class EmailMonitorConfig extends BaseVo {

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
	private int supperid;// 供应商id snow add at 2010-5-21

	/**
	 * 接收邮件网关 mail.dhcc.com.cn
	 */
	private String receiveAddress;

	public String getAddress() {
		return address;
	}

	public String getBid() {
		return bid;
	}

	public int getFlag() {
		return flag;
	}

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

	public String getReceiveAddress() {
		return receiveAddress;
	}

	public String getRecivemail() {
		return recivemail;
	}

	public String getSendemail() {
		return sendemail;
	}

	public String getSendmobiles() {
		return sendmobiles;
	}

	public String getSendphone() {
		return sendphone;
	}

	public int getSupperid() {
		return supperid;
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

	public void setBid(String bid) {
		this.bid = bid;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

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

	public void setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
	}

	public void setRecivemail(String recivemail) {
		this.recivemail = recivemail;
	}

	public void setSendemail(String sendemail) {
		this.sendemail = sendemail;
	}

	public void setSendmobiles(String sendmobiles) {
		this.sendmobiles = sendmobiles;
	}

	public void setSendphone(String sendphone) {
		this.sendphone = sendphone;
	}

	public void setSupperid(int supperid) {
		this.supperid = supperid;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}