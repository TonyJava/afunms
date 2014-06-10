package com.afunms.config.model;

public class CheckResult {
	private int id;
	private String ip;
	private String name;
	private int count0;
	private int count1;
	private int count2;
	private int exactCount;
	private String status;
	private String checkTime;

	public String getCheckTime() {
		return checkTime;
	}

	public int getCount0() {
		return count0;
	}

	public int getCount1() {
		return count1;
	}

	public int getCount2() {
		return count2;
	}

	public int getExactCount() {
		return exactCount;
	}

	public int getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public String getName() {
		return name;
	}

	public String getStatus() {
		return status;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public void setCount0(int count0) {
		this.count0 = count0;
	}

	public void setCount1(int count1) {
		this.count1 = count1;
	}

	public void setCount2(int count2) {
		this.count2 = count2;
	}

	public void setExactCount(int exactCount) {
		this.exactCount = exactCount;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
