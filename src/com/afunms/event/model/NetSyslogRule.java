/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.model;

import com.afunms.common.base.BaseVo;

public class NetSyslogRule extends BaseVo {
	private Long id;
	private String facility;// 事件来源
	private String priority;// 优先级

	public String getFacility() {
		return facility;
	}

	public Long getId() {
		return id;
	}

	public String getPriority() {
		return priority;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public void setId(Long l) {
		id = l;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
}
