/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.model;

import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetSyslogEvent extends BaseVo {
	private Long id;
	private String ipaddress;// IP��ַ
	private String hostname;// ������
	private String message;// ����Ϣ����
	private int facility;// �¼���Դ
	private int priority;// ���ȼ�
	private String facilityName;// �¼���Դ����
	private String priorityName;// ���ȼ�����
	private int processId;
	private String processName;
	private String processIdStr;
	private Calendar recordtime;// ʱ���
	private String username;// �¼���Դ
	private int eventid; // �豸����

	public int getEventid() {
		return eventid;
	}

	public int getFacility() {
		return facility;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public String getHostname() {
		return hostname;
	}

	public Long getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getMessage() {
		return message;
	}

	public int getPriority() {
		return priority;
	}

	public String getPriorityName() {
		return priorityName;
	}

	public int getProcessId() {
		return processId;
	}

	public String getProcessIdStr() {
		return processIdStr;
	}

	public String getProcessName() {
		return processName;
	}

	public Calendar getRecordtime() {
		return recordtime;
	}

	public String getUsername() {
		return username;
	}

	public void setEventid(int eventid) {
		this.eventid = eventid;
	}

	public void setFacility(int facility) {
		this.facility = facility;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setId(Long l) {
		id = l;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setPriorityName(String priorityName) {
		this.priorityName = priorityName;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}

	public void setProcessIdStr(String processIdStr) {
		this.processIdStr = processIdStr;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public void setRecordtime(Calendar recordtime) {
		this.recordtime = recordtime;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
