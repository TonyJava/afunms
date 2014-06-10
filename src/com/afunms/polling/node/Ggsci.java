/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;


public class Ggsci extends Application {

	private int id;
	private int flag;
	private int mon_flag;
	private String sendmobiles;
	private String netid;
	private String sendemail;
	private String sendphone;

	public int getFlag() {
		return flag;
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

	public void setFlag(int flag) {
		this.flag = flag;
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