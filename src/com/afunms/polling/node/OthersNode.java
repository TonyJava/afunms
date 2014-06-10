/**
 * <p>Description:mapping table nms_hint_node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2010-01-4
 */

package com.afunms.polling.node;

public class OthersNode extends Application {

	private String name;

	private String ipaddress;

	private String alais;

	private String sendmobiles;

	private String sendemail;

	private String sendphone;

	private String bid;

	private int managed;

	public String getAlais() {
		return alais;
	}

	@Override
	public String getBid() {
		return bid;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public int getManaged() {
		return managed;
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

	public void setAlais(String alais) {
		this.alais = alais;
	}

	@Override
	public void setBid(String bid) {
		this.bid = bid;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setManaged(int managed) {
		this.managed = managed;
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

}
