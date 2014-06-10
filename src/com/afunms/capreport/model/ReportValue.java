package com.afunms.capreport.model;

import java.util.*;

@SuppressWarnings("unchecked")
public class ReportValue {
	private String ip;// IP地址
	private String type;// 类型

	private List ipList;
	private List listValue;// 数值
	private List listTemp;// 备用

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

	public List getIpList() {
		return ipList;
	}

	public void setIpList(List ipList) {
		this.ipList = ipList;
	}

	public List getListValue() {
		return listValue;
	}

	public void setListValue(List listValue) {
		this.listValue = listValue;
	}

	public List getListTemp() {
		return listTemp;
	}

	public void setListTemp(List listTemp) {
		this.listTemp = listTemp;
	}

}
