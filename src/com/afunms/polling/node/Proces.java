/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;


public class Proces extends Application {

	private int id;
	private String name;
	private String ipaddress;
	private String sendmobiles;
	private String sendemail;
	private String sendphone;
	private int supperid;// π©”¶…Ãid snow add at 2010-5-27

	@Override
	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getName() {
		return name;
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

	@Override
	public int getSupperid() {
		return supperid;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public void setSupperid(int supperid) {
		this.supperid = supperid;
	}

}