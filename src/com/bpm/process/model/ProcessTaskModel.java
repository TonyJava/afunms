package com.bpm.process.model;

public class ProcessTaskModel {
	private String name;
	private String starttime ;
	private String endtime;
	private String personname;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getPersonname() {
		return personname;
	}
	public void setPersonname(String personname) {
		this.personname = personname;
	}

}
