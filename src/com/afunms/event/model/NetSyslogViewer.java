/**
 * @author zhangys
 * @project afunms
 * @date 2011-07
 */
package com.afunms.event.model;

import com.afunms.common.base.BaseVo;

public class NetSyslogViewer extends BaseVo {
	private long id;
	private String status;// ���״̬
	private int category;// �豸����
	private String hostName;// ������
	private String ipaddress;// IP��ַ
	private int errors;// ������
	private int warnings;// ������
	private int failures;// ʧ����
	private int others;// ����
	private int all;// ȫ��

	public int getAll() {
		return all;
	}

	public int getCategory() {
		return category;
	}

	public int getErrors() {
		return errors;
	}

	public int getFailures() {
		return failures;
	}

	public String getHostName() {
		return hostName;
	}

	public long getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public int getOthers() {
		return others;
	}

	public String getStatus() {
		return status;
	}

	public int getWarnings() {
		return warnings;
	}

	public void setAll(int all) {
		this.all = all;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public void setFailures(int failures) {
		this.failures = failures;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setId(long l) {
		id = l;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setOthers(int others) {
		this.others = others;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setWarnings(int warnings) {
		this.warnings = warnings;
	}
}
