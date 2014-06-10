package com.afunms.application.model;

import java.util.Hashtable;

import com.afunms.common.base.BaseVo;

@SuppressWarnings("unchecked")
public class MonitorDBDTO extends BaseVo {

	private int id;

	private String ipAddress;

	private String alias;

	private String sid;

	private String status;

	private String dbtype;

	private String mon_flag;

	private String dbname;

	private String port;

	private String pingValue;

	private Hashtable eventListSummary;

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @return the dbname
	 */
	public String getDbname() {
		return dbname;
	}

	/**
	 * @return the dbtype
	 */
	public String getDbtype() {
		return dbtype;
	}

	/**
	 * @return the eventListSummary
	 */
	public Hashtable getEventListSummary() {
		return eventListSummary;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @return the mon_flag
	 */
	public String getMon_flag() {
		return mon_flag;
	}

	/**
	 * @return the pingValue
	 */
	public String getPingValue() {
		return pingValue;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @return the sid
	 */
	public String getSid() {
		return sid;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param alias
	 *            the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @param dbname
	 *            the dbname to set
	 */
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	/**
	 * @param dbtype
	 *            the dbtype to set
	 */
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}

	/**
	 * @param eventListSummary
	 *            the eventListSummary to set
	 */
	public void setEventListSummary(Hashtable eventListSummary) {
		this.eventListSummary = eventListSummary;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @param mon_flag
	 *            the mon_flag to set
	 */
	public void setMon_flag(String mon_flag) {
		this.mon_flag = mon_flag;
	}

	/**
	 * @param pingValue
	 *            the pingValue to set
	 */
	public void setPingValue(String pingValue) {
		this.pingValue = pingValue;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @param sid
	 *            the sid to set
	 */
	public void setSid(String sid) {
		this.sid = sid;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
