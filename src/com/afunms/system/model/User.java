/**
 * <p>Description:mapping table NMS_USER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class User extends BaseVo {
	private int id;
	private String userid;
	private String name;
	private String password;
	private int sex;
	private int role;
	private String phone;
	private String mobile;
	private String email;
	private int dept;
	private int position;
	private String businessids;
	private String skins;
	private String group;
	private String deptname;
	private String positionname;
	private String rolename;

	public String getBusinessids() {
		return businessids;
	}

	public int getDept() {
		return dept;
	}

	public String getDeptname() {
		return deptname;
	}

	public String getEmail() {
		return email;
	}

	public String getGroup() {
		return group;
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

	public String getPassword() {
		return password;
	}

	public String getPhone() {
		return phone;
	}

	public int getPosition() {
		return position;
	}

	public String getPositionname() {
		return positionname;
	}

	public int getRole() {
		return role;
	}

	public String getRolename() {
		return rolename;
	}

	public int getSex() {
		return sex;
	}

	public String getSkins() {
		return skins;
	}

	public String getUserid() {
		return userid;
	}

	public void setBusinessids(String businessids) {
		this.businessids = businessids;
	}

	public void setDept(int dept) {
		this.dept = dept;
	}

	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setGroup(String group) {
		this.group = group;
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

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setPositionname(String positionname) {
		this.positionname = positionname;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public void setSkins(String skins) {
		this.skins = skins;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

}
