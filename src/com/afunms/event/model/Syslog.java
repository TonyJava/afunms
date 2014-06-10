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

public class Syslog extends BaseVo {
	private Long id;

	private int processid;// ����id
	private String processname;// ������
	private String processidstr;// ����id�ַ�

	private int facility;// �¼���Դ
	private int priority;// ���ȼ�
	private String facilityName;// �¼���Դ����
	private String priorityName;// ���ȼ�����
	private String hostname;// ������
	private Calendar recordtime;// ʱ���
	private String message;// ����Ϣ����
	private String ipaddress;// IP��ַ
	private String businessid;// ҵ����Դ

	private int eventid;// �¼���Դ
	private String username;

	public String getBusinessid() {
		return businessid;
	}

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

	public int getProcessid() {
		return processid;
	}

	public String getProcessidstr() {
		return processidstr;
	}

	public String getProcessname() {
		return processname;
	}

	public Calendar getRecordtime() {
		return recordtime;
	}

	public String getUsername() {
		return username;
	}

	public void setBusinessid(String businessid) {
		this.businessid = businessid;
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

	public void setProcessid(int processid) {
		this.processid = processid;
	}

	public void setProcessidstr(String processidstr) {
		this.processidstr = processidstr;
	}

	public void setProcessname(String processname) {
		this.processname = processname;
	}

	public void setRecordtime(Calendar recordtime) {
		this.recordtime = recordtime;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
