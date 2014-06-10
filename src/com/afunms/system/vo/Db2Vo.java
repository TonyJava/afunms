package com.afunms.system.vo;

public class Db2Vo {

	private String tablespace;

	private String size;

	private String free;

	private String percent;

	public String getFree() {
		return free;
	}

	public String getPercent() {
		return percent;
	}

	public String getSize() {
		return size;
	}

	public String getTablespace() {
		return tablespace;
	}

	public void setFree(String free) {
		this.free = free;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setTablespace(String tablespace) {
		this.tablespace = tablespace;
	}

}