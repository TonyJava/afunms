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

public class NetSyslog extends BaseVo {
	private Long id;

	private int facility;// �¼���Դ
	private int priority;// ���ȼ�
	private String facilityName;// �¼���Դ����
	private String priorityName;// ���ȼ�����
	private String hostname;// ������
	private Calendar recordtime;// ʱ���
	private String message;// ����Ϣ����
	private String ipaddress;// IP��ַ
	private String businessid;// �¼���Դ
	private int category; // �豸����

	public String getBusinessid() {
		return businessid;
	}

	public int getCategory() {
		return category;
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

	public Calendar getRecordtime() {
		return recordtime;
	}

	public void setBusinessid(String businessid) {
		this.businessid = businessid;
	}

	public void setCategory(int category) {
		this.category = category;
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

	public void setRecordtime(Calendar recordtime) {
		this.recordtime = recordtime;
	}

}
