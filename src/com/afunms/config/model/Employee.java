/**
 * <p>Description:mapping table NMS_USER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class Employee extends BaseVo {
	private int id;
	private String name;
	private int sex;
	private String phone;
	private String mobile;
	private String email;
	private int dept;
	private int position;
	private String businessids;

	public String getBusinessids() {
		return businessids;
	}

	public int getDept() {
		return dept;
	}

	public String getEmail() {
		return email;
	}

	public int getId() {
		return id;
	}

	public String getMobile() {
		return mobile;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public int getPosition() {
		return position;
	}

	public int getSex() {
		return sex;
	}

	public void setBusinessids(String businessids) {
		this.businessids = businessids;
	}

	public void setDept(int dept) {
		this.dept = dept;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}
}
