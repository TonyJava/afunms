package com.afunms.detail.reomte.model;

import com.afunms.common.base.BaseVo;

public class ProcessInfo extends BaseVo {

	private String pid; // ����id

	private String name; // ��������

	private String count; // ���̸���

	private String type; // ��������

	private String cpuTime; // CPUʱ��

	private String memoryUtilization; // �ڴ�ռ����

	private String memory; // �ڴ�ռ����

	private String status; // ��ǰ״̬

	private String averageUSecs; // ƽ������CPU����ʱ��(���˽�����)

	private String extPriorityRev; // ���ȼ�(���˽�����)

	private String runtime; // ����ʱ��(���˽�����)

	/**
	 * @return the averageUSecs
	 */
	public String getAverageUSecs() {
		return averageUSecs;
	}

	/**
	 * @return the count
	 */
	public String getCount() {
		return count;
	}

	/**
	 * @return the cpuTime
	 */
	public String getCpuTime() {
		return cpuTime;
	}

	/**
	 * @return the extPriorityRev
	 */
	public String getExtPriorityRev() {
		return extPriorityRev;
	}

	/**
	 * @return the memory
	 */
	public String getMemory() {
		return memory;
	}

	/**
	 * @return the memoryUtilization
	 */
	public String getMemoryUtilization() {
		return memoryUtilization;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * @return the runtime
	 */
	public String getRuntime() {
		return runtime;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param averageUSecs
	 *            the averageUSecs to set
	 */
	public void setAverageUSecs(String averageUSecs) {
		this.averageUSecs = averageUSecs;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(String count) {
		this.count = count;
	}

	/**
	 * @param cpuTime
	 *            the cpuTime to set
	 */
	public void setCpuTime(String cpuTime) {
		this.cpuTime = cpuTime;
	}

	/**
	 * @param extPriorityRev
	 *            the extPriorityRev to set
	 */
	public void setExtPriorityRev(String extPriorityRev) {
		this.extPriorityRev = extPriorityRev;
	}

	/**
	 * @param memory
	 *            the memory to set
	 */
	public void setMemory(String memory) {
		this.memory = memory;
	}

	/**
	 * @param memoryUtilization
	 *            the memoryUtilization to set
	 */
	public void setMemoryUtilization(String memoryUtilization) {
		this.memoryUtilization = memoryUtilization;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param pid
	 *            the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * @param runtime
	 *            the runtime to set
	 */
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
