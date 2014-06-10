/**
 * <p>Description:mapping table NMS_USER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class AlertInfoServer extends BaseVo {
	private int id;
	private String ipaddress;
	private String port;
	private String desc;
	private int flag;

	public String getDesc() {
		return desc;
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

	public String getPort() {
		return port;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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

	public void setPort(String port) {
		this.port = port;
	}

}
