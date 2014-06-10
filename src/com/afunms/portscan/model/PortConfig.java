package com.afunms.portscan.model;

import com.afunms.common.base.BaseVo;

public class PortConfig extends BaseVo {

	private int id;

	/**
	 * 端口所属的ip
	 */
	private String ipaddress;

	/**
	 * 端口号
	 */
	private String port;

	/**
	 * 端口名
	 */
	private String portName;

	/**
	 * 端口描述
	 */
	private String description;

	/**
	 * 端口类型
	 */
	private String type;

	/**
	 * 超时时间
	 */
	private String timeout;

	/**
	 * 端口状态 "0" 未开启 "1" 已开启
	 */
	private String status;

	/**
	 * 扫描状态 "0" 未扫描 "1" 已扫描
	 */
	private String isScanned;

	/**
	 * 上一次扫描时间
	 */
	private String scantime;

	/**
	 * 
	 */
	public PortConfig() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param ipaddress
	 * @param port
	 * @param portName
	 * @param description
	 * @param type
	 * @param timeout
	 * @param status
	 * @param isScanned
	 * @param scantime
	 */
	public PortConfig(int id, String ipaddress, String port, String portName, String description, String type, String timeout, String status, String isScanned, String scantime) {
		this.id = id;
		this.ipaddress = ipaddress;
		this.port = port;
		this.portName = portName;
		this.description = description;
		this.type = type;
		this.timeout = timeout;
		this.status = status;
		this.isScanned = isScanned;
		this.scantime = scantime;
	}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getIsScanned() {
		return isScanned;
	}

	public String getPort() {
		return port;
	}

	public String getPortName() {
		return portName;
	}

	public String getScantime() {
		return scantime;
	}

	public String getStatus() {
		return status;
	}

	public String getTimeout() {
		return timeout;
	}

	public String getType() {
		return type;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setIsScanned(String isScanned) {
		this.isScanned = isScanned;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public void setScantime(String scantime) {
		this.scantime = scantime;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public void setType(String type) {
		this.type = type;
	}

}
