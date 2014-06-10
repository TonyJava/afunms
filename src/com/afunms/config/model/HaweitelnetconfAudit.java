package com.afunms.config.model;

import java.sql.Timestamp;

import com.afunms.common.base.BaseVo;

public class HaweitelnetconfAudit extends BaseVo {

	private int id;// id
	private String ip;// ip地址
	private String username;
	private int userid;
	private String oldpassword;// 旧用户密码
	private String newpassword;// 新用户密码
	private Timestamp dotime;// 时间
	private String bak;//

	public String getBak() {
		return bak;
	}

	public Timestamp getDotime() {
		return dotime;
	}

	public int getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public String getOldpassword() {
		return oldpassword;
	}

	public int getUserid() {
		return userid;
	}

	public String getUsername() {
		return username;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public void setDotime(Timestamp dotime) {
		this.dotime = dotime;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public void setOldpassword(String oldpassword) {
		this.oldpassword = oldpassword;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
