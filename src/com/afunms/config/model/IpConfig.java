package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

/**
 * 
 * ip 地址管理bean
 * 
 * @author
 * 
 */
public class IpConfig extends BaseVo {

	private int id; // id
	private String ipaddress;// ip 地址
	private int discrictid; // 地区id
	private int deptid; // 部门id
	private int employeeid; // 员工id
	private String ipdesc; // ip 址描述

	public int getDeptid() {
		return deptid;
	}

	public int getDiscrictid() {
		return discrictid;
	}

	public int getEmployeeid() {
		return employeeid;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getIpdesc() {
		return ipdesc;
	}

	public void setDeptid(int deptid) {
		this.deptid = deptid;
	}

	public void setDiscrictid(int discrictid) {
		this.discrictid = discrictid;
	}

	public void setEmployeeid(int employeeid) {
		this.employeeid = employeeid;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ip) {
		this.ipaddress = ip;
	}

	public void setIpdesc(String ipdesc) {
		this.ipdesc = ipdesc;
	}

}
