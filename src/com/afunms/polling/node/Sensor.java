package com.afunms.polling.node;

/**
 * 
 * @descrition ´«¸ÐÆ÷
 * @author wangxiangyong
 * @date Jun 16, 2013 5:40:50 PM
 */
public class Sensor {
	private String name;
	private String value;
	private String status;

	public String getName() {
		return name;
	}

	public String getStatus() {
		return status;
	}

	public String getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
