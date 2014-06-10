/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;

public class SocketService extends Application {
	private int id;
	private String ipaddress;
	private String port;
	private String portdesc;
	private int monflag;
	private int flag;
	private int timeout;
	private String bid;
	private String sendmobiles;
	private String sendemail;
	private String sendphone;

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

	public String getPort() {
		return port;
	}

	public String getPortdesc() {
		return portdesc;
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

	public void setPort(String port) {
		this.port = port;
	}

	public void setPortdesc(String portdesc) {
		this.portdesc = portdesc;
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
}