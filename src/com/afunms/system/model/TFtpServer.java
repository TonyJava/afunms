/**
 * <p>Description:mapping table NMS_USER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class TFtpServer extends BaseVo {
	private int id;
	private String ip;
	private int usedflag;

	public int getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public int getUsedflag() {
		return usedflag;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setUsedflag(int usedflag) {
		this.usedflag = usedflag;
	}

}
