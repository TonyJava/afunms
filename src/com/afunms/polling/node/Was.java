/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;


public class Was extends Application {

	private int id;
	private String name;
	private String ipaddress;
	private String community;
	private int portnum;
	private String sendmobiles;
	private int mon_flag;
	private String netid;
	private String sendemail;
	private String sendphone;

	public String getCommunity() {
		return community;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public int getMon_flag() {
		return mon_flag;
	}

	public String getName() {
		return name;
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

	public void setCommunity(String community) {
		this.community = community;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setMon_flag(int mon_flag) {
		this.mon_flag = mon_flag;
	}

	public void setName(String name) {
		this.name = name;
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

}