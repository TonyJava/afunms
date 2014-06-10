/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;

/**
 * 
 * @author GANYI
 * @since 2012-04-23 18:51:00
 */
public class TFtp extends Application {

	private int id;

	private String name;

	private String username;

	private String password;

	/**
	 * 超时
	 */
	private int timeout;

	/**
	 * 是否监视 0为false 1为true
	 */
	private int monflag;

	/**
	 * ip地址
	 */
	private String ipaddress;

	/**
	 * 文件名
	 */
	private String filename;

	/**
	 * 所属业务
	 */
	private String bid;

	/**
	 * 短信接收人
	 */
	private String sendmobiles;

	/**
	 * email接收人
	 */
	private String sendemail;

	/**
	 * 电话接收人
	 */
	private String sendphone;

	/**
	 * @return the bid
	 */
	@Override
	public String getBid() {
		return bid;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the id
	 */
	@Override
	public int getId() {
		return id;
	}

	/**
	 * @return the ipaddress
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	/**
	 * @return the monflag
	 */
	public int getMonflag() {
		return monflag;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the sendemail
	 */
	@Override
	public String getSendemail() {
		return sendemail;
	}

	/**
	 * @return the sendmobiles
	 */
	@Override
	public String getSendmobiles() {
		return sendmobiles;
	}

	/**
	 * @return the sendphone
	 */
	@Override
	public String getSendphone() {
		return sendphone;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param bid
	 *            the bid to set
	 */
	@Override
	public void setBid(String bid) {
		this.bid = bid;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	@Override
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param ipaddress
	 *            the ipaddress to set
	 */
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	/**
	 * @param monflag
	 *            the monflag to set
	 */
	public void setMonflag(int monflag) {
		this.monflag = monflag;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param sendemail
	 *            the sendemail to set
	 */
	@Override
	public void setSendemail(String sendemail) {
		this.sendemail = sendemail;
	}

	/**
	 * @param sendmobiles
	 *            the sendmobiles to set
	 */
	@Override
	public void setSendmobiles(String sendmobiles) {
		this.sendmobiles = sendmobiles;
	}

	/**
	 * @param sendphone
	 *            the sendphone to set
	 */
	@Override
	public void setSendphone(String sendphone) {
		this.sendphone = sendphone;
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

}