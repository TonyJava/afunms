/**
 * <p>Description: 门禁系统记录</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project 齐鲁石化
 * @date 2007-01-18
 */

package com.afunms.security.model;

import com.afunms.common.base.BaseVo;

public class GateRecord extends BaseVo {
	private String person; // 姓名
	private String event; // 事件
	private String logTime; // 进出时间(包括日期)
	private String io; // 1=出门,0=进门

	public String getEvent() {
		return event;
	}

	public String getIo() {
		return io;
	}

	public String getLogTime() {
		return logTime;
	}

	public String getPerson() {
		return person;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public void setIo(String io) {
		this.io = io;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	public void setPerson(String person) {
		this.person = person;
	}
}
