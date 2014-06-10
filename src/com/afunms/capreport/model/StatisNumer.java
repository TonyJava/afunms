package com.afunms.capreport.model;

/**
 * 统计设备的性能指标
 * 
 * @author wxy
 * 
 */
public class StatisNumer {
	private String ip;// ip地址
	private String type;// 类型
	private String name;// 名称
	private String current;// 当前值
	private String average;// 平均值
	private String maximum;// 最大值
	private String mininum;// 最小值

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public String getAverage() {
		return average;
	}

	public void setAverage(String average) {
		this.average = average;
	}

	public String getMaximum() {
		return maximum;
	}

	public void setMaximum(String maximum) {
		this.maximum = maximum;
	}

	public String getMininum() {
		return mininum;
	}

	public void setMininum(String mininum) {
		this.mininum = mininum;
	}

}
