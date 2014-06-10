package com.afunms.polling.om;

public class ProcessInfo implements Cloneable {
	private String pid;
	private Object CpuUtilization;
	private String Type;
	private String StartTime;
	private Object CpuTime;
	private String USER;
	private Object MemoryUtilization;
	private String Name;
	private String Status;
	private Object Memory;
	private String threadCount;
	private String handleCount;
	private int count;

	@Override
	public ProcessInfo clone() {
		ProcessInfo p = null;
		try {
			p = (ProcessInfo) super.clone();
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			return p;
		}
	}

	public int getCount() {
		return count;
	}

	public Object getCpuTime() {
		return CpuTime;
	}

	public Object getCpuUtilization() {
		return CpuUtilization;
	}

	public String getHandleCount() {
		return handleCount;
	}

	public Object getMemory() {
		return Memory;
	}

	public Object getMemoryUtilization() {
		return MemoryUtilization;
	}

	public String getName() {
		return Name;
	}

	public String getPid() {
		return pid;
	}

	public String getStartTime() {
		return StartTime;
	}

	public String getStatus() {
		return Status;
	}

	public String getThreadCount() {
		return threadCount;
	}

	public String getType() {
		return Type;
	}

	public String getUSER() {
		return USER;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setCpuTime(Object cpuTime) {
		CpuTime = cpuTime;
	}

	public void setCpuUtilization(Object cpuUtilization) {
		CpuUtilization = cpuUtilization;
	}

	public void setHandleCount(String handleCount) {
		this.handleCount = handleCount;
	}

	public void setMemory(Object memory) {
		Memory = memory;
	}

	public void setMemoryUtilization(Object memoryUtilization) {
		MemoryUtilization = memoryUtilization;
	}

	public void setName(String name) {
		Name = name;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setStartTime(String startTime) {
		StartTime = startTime;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public void setThreadCount(String threadCount) {
		this.threadCount = threadCount;
	}

	public void setType(String type) {
		Type = type;
	}

	public void setUSER(String user) {
		USER = user;
	}

}
